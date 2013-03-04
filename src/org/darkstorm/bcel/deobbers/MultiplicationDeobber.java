package org.darkstorm.bcel.deobbers;

import java.util.*;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.*;

public class MultiplicationDeobber extends Deobber {
	private int clearedMultiplies;

	public MultiplicationDeobber(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		int amount = 0;
		for(Method m : classGen.getMethods()) {
			if(m.isAbstract())
				continue;
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if(iList == null)
				continue;
			Stack<Object> stack = new Stack<Object>();
			List<InstructionHandle> toDelete = new ArrayList<InstructionHandle>();
			List<InstructionHandle> alreadyVisited = new ArrayList<InstructionHandle>();
			analyzeStack(classGen, m, iList, 0, stack, toDelete, alreadyVisited);
			amount += toDelete.size();
			for(InstructionHandle handle : toDelete)
				updateTargeters(handle, toDelete);
			for(InstructionHandle handle : toDelete) {
				try {
					iList.delete(handle);
				} catch(TargetLostException exception) {
					exception.printStackTrace();
				}
				amount++;
			}

			iList.setPositions();
			methodGen.setInstructionList(iList);
			methodGen.setMaxLocals();
			methodGen.setMaxStack();
			classGen.replaceMethod(m, methodGen.getMethod());
		}
		clearedMultiplies += amount;
		System.out.println("Cleared " + amount + " multiplies in class "
				+ classGen.getClassName() + ".");
	}

	private void updateTargeters(InstructionHandle handle,
			List<InstructionHandle> allHandles) {
		if(handle.getInstruction() instanceof LDC) {
			if(handle.hasTargeters()) {
				if(handle.getNext().getInstruction() instanceof IMUL
						&& !allHandles.contains(handle.getPrev())) {
					for(InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getPrev());
				} else {
					for(InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getNext());
					if(allHandles.contains(handle.getNext()))
						updateTargeters(handle.getNext(), allHandles);
				}
			}
		} else if(handle.getInstruction() instanceof LDC2_W) {
			if(handle.hasTargeters()) {
				if(handle.getNext().getInstruction() instanceof LMUL
						&& !allHandles.contains(handle.getPrev())) {
					for(InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getPrev());
				} else {
					for(InstructionTargeter targeter : handle.getTargeters())
						targeter.updateTarget(handle, handle.getNext());
					if(allHandles.contains(handle.getNext()))
						updateTargeters(handle.getNext(), allHandles);
				}
			}
		} else if(handle.getInstruction() instanceof IMUL
				|| handle.getInstruction() instanceof LMUL) {
			if(handle.hasTargeters()) {
				for(InstructionTargeter targeter : handle.getTargeters())
					targeter.updateTarget(handle, handle.getNext());
				if(allHandles.contains(handle.getNext()))
					updateTargeters(handle.getNext(), allHandles);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analyzeStack(ClassGen classGen, Method m,
			InstructionList list, int startingPosition, Stack<Object> stack,
			List<InstructionHandle> toDelete,
			List<InstructionHandle> alreadyVisited) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		stack = (Stack<Object>) stack.clone();
		Instruction[] instructions = list.getInstructions();
		InstructionHandle[] handles = list.getInstructionHandles();
		for(int i = startingPosition; i < instructions.length; i++) {
			Instruction ins = instructions[i];
			InstructionHandle handle = handles[i];
			int consumed = ins.consumeStack(cpg);
			int produced = ins.produceStack(cpg);
			for(int j = 0; j < consumed; j++) {
				try {
					Object o = stack.pop();
					if(o instanceof InstructionHandle) {
						InstructionHandle otherHandle = (InstructionHandle) o;
						Instruction otherInstruction = otherHandle
								.getInstruction();
						if(!((ins instanceof IMUL && otherInstruction instanceof LDC) || (ins instanceof LMUL && otherInstruction instanceof LDC2_W)))
							continue;
						if(!toDelete.contains(handle))
							toDelete.add(handle);
						if(!toDelete.contains(otherHandle))
							toDelete.add(otherHandle);
					}
				} catch(EmptyStackException exception) {
					String mStr = m.toString();
					String[] parts = mStr.split("\\(")[0].split(" ");
					String string = classGen.getClassName() + "."
							+ parts[parts.length - 1] + "("
							+ mStr.split("\\(")[1];
					System.err.println("Empty stack: " + handle.toString(true)
							+ " in " + string);
					throw exception;
				}
			}
			for(int j = 0; j < produced; j++) {
				if(ins instanceof LDC && ((LDC) ins).getType(cpg) == Type.INT) {
					stack.push(handle);
				} else if(ins instanceof LDC2_W
						&& ((LDC2_W) ins).getType(cpg) == Type.LONG) {
					stack.push(handle);
				} else
					stack.push(new Object());
			}
			if(consumed == Constants.UNPREDICTABLE)
				throw new RuntimeException("Cannot consume: " + ins);
			if(produced == Constants.UNPREDICTABLE)
				throw new RuntimeException("Cannot produce: " + ins);
			if(ins instanceof BranchInstruction) {
				if(alreadyVisited.contains(handle))
					break;
				alreadyVisited.add(handle);
				InstructionHandle target = list.findHandle(handle.getPosition()
						+ ((BranchInstruction) ins).getIndex());
				int newIndex = indexOf(list, target);
				if(!(ins instanceof UnconditionalBranch)) {
					analyzeStack(classGen, m, list, newIndex, stack, toDelete,
							alreadyVisited);
				} else {
					if(newIndex == i)
						continue;
					i = newIndex - 1;
					continue;
				}
			}
			if(ins instanceof ReturnInstruction)
				break;
		}
	}

	private int indexOf(InstructionList list, InstructionHandle handle) {
		InstructionHandle[] handles = list.getInstructionHandles();
		for(int i = 0; i < handles.length; i++)
			if(handles[i] == handle)
				return i;
		throw new ArrayIndexOutOfBoundsException();
	}

	@Override
	public void finish() {
		System.out.println("Cleared " + clearedMultiplies + " multiplies.");
	}
}
