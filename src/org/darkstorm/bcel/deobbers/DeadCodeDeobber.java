package org.darkstorm.bcel.deobbers;

import java.util.*;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.*;

public class DeadCodeDeobber extends Deobber {
	private int deadCodeRemoved;

	public DeadCodeDeobber(Injector injector) {
		super(injector);
	}

	@Override
	public void deob(ClassGen classGen) {
		ConstantPoolGen cpg = classGen.getConstantPool();
		for(Method m : classGen.getMethods()) {
			if(m.isAbstract())
				continue;
			MethodGen methodGen = new MethodGen(m, classGen.getClassName(), cpg);
			InstructionList iList = methodGen.getInstructionList();
			if(iList == null)
				continue;
			Instruction[] instructions = iList.getInstructions();
			InstructionHandle[] handles = iList.getInstructionHandles();
			List<InstructionHandle> toDelete = new ArrayList<InstructionHandle>();
			instructionLoop: for(int i = 0; i < instructions.length; i++) {
				Instruction ins = instructions[i];
				InstructionHandle handle = handles[i];
				if(ins instanceof BranchInstruction
						&& ins instanceof UnconditionalBranch) {
					InstructionHandle target = iList.findHandle(handle
							.getPosition()
							+ ((BranchInstruction) ins).getIndex());
					int newIndex = indexOf(iList, target);
					if(newIndex < i)
						continue;
					if((newIndex == i && !handle.hasTargeters())
							|| newIndex == i + 1) {
						if(!toDelete.contains(handle))
							toDelete.add(handle);
						if(handle.hasTargeters()) {
							for(InstructionTargeter targeter : handle
									.getTargeters())
								targeter.updateTarget(handle, handle.getNext());
						}
						continue;
					}
					for(int j = i + 1; j < newIndex; j++) {
						if(handles[i].hasTargeters())
							continue instructionLoop;
						if(!toDelete.contains(handles[i]))
							toDelete.add(handles[i]);
					}
				}
			}
			for(InstructionHandle handle : toDelete) {
				try {
					iList.delete(handle);
				} catch(TargetLostException exception) {
					exception.printStackTrace();
				}
				deadCodeRemoved++;
			}
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
		System.out.println("Cleared " + deadCodeRemoved
				+ " unused instructions.");
	}

}
