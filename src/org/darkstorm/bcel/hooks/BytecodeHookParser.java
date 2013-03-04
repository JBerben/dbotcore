package org.darkstorm.bcel.hooks;

import java.util.*;

import java.lang.reflect.Constructor;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.util.InstructionSearcher;
import org.jdom.Element;

public class BytecodeHookParser {
	private ClassGen classGen;
	private MethodGen method;
	private InstructionList instructions;
	private Stack<Integer> constIndexStack;
	private Map<InstructionHandle, Integer> toUpdate;
	private int startingIndex;
	private int insertedCount = 0;

	public BytecodeHookParser(ClassGen classGen, MethodGen method,
			int startingIndex) {
		this.classGen = classGen;
		this.method = method;
		instructions = this.method.getInstructionList();
		constIndexStack = new Stack<Integer>();
		toUpdate = new HashMap<InstructionHandle, Integer>();
		this.startingIndex = startingIndex;
	}

	public void parseNext(Element instructionElement) {
		// <instruction type="addconst" const="String" arguments="hello" />
		// <instruction type="GETFIELD" arguments="const" />
		// <instruction type="IFEQ" arguments="pos1" />
		String type = instructionElement.getAttributeValue("type");
		if(type.equals("addconst")) {
			ConstantPoolGen constantPool = classGen.getConstantPool();
			String className = instructionElement.getAttributeValue("const");
			if(className.equals("String")) {
				String value = instructionElement
						.getAttributeValue("arguments");
				constIndexStack.push(constantPool.addString(value));
			} else if(className.equals("Integer")) {
				int value = Integer.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addInteger(value));
			} else if(className.equals("Float")) {
				float value = Float.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addFloat(value));
			} else if(className.equals("Double")) {
				double value = Double.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addDouble(value));
			} else if(className.equals("Long")) {
				long value = Long.valueOf(instructionElement
						.getAttributeValue("arguments"));
				constIndexStack.push(constantPool.addLong(value));
			} else if(className.equals("Fieldref")) {
				String arguments = instructionElement
						.getAttributeValue("arguments");
				String[] fieldInfo = arguments.split(":");
				constIndexStack.push(constantPool.addFieldref(fieldInfo[0],
						fieldInfo[1], fieldInfo[2]));
			} else if(className.equals("Methodref")) {
				String arguments = instructionElement
						.getAttributeValue("arguments");
				String[] methodInfo = arguments.split(":");
				constIndexStack.push(constantPool.addMethodref(methodInfo[0],
						methodInfo[1], methodInfo[2]));
			} else if(className.equals("InterfaceMethodref")) {
				String arguments = instructionElement
						.getAttributeValue("arguments");
				String[] methodInfo = arguments.split(":");
				constIndexStack.push(constantPool.addInterfaceMethodref(
						methodInfo[0], methodInfo[1], methodInfo[2]));
			} else if(className.equals("Class")) {
				String c = instructionElement.getAttributeValue("arguments");
				constIndexStack.push(constantPool.addClass((ObjectType) Type
						.getType(c)));
			} else if(className.equals("ArrayClass")) {
				String c = instructionElement.getAttributeValue("arguments");
				constIndexStack.push(constantPool
						.addArrayClass((ArrayType) Type.getType(c)));
			}
		} else {
			try {
				Class<?> instructionClass = Class
						.forName("org.apache.bcel.generic." + type);
				String argumentsTogether = instructionElement
						.getAttributeValue("arguments");
				String[] arguments = argumentsTogether.split(":");
				Vector<Class<?>> argumentClasses = new Vector<Class<?>>();
				Vector<Object> argumentObjects = new Vector<Object>();
				boolean needsUpdate = false;
				int updateOffset = 0;
				for(String argument : arguments) {
					try {
						int amount = Integer.parseInt(argument);
						argumentClasses.add(Integer.TYPE);
						argumentObjects.add(amount);
					} catch(NumberFormatException exception) {
						if(argument.startsWith("pos")) {
							argumentClasses.add(InstructionHandle.class);
							int pos = Integer.parseInt(argument.substring(3));
							if(instructions.getInstructionHandles().length > pos
									+ insertedCount) {
								InstructionHandle handle = instructions
										.getInstructionHandles()[pos
										+ insertedCount];
								argumentObjects.add(handle);
							} else {
								argumentObjects.add(null);
								needsUpdate = true;
								updateOffset = pos;
							}
						} else if(argument.equals("const")) {
							argumentClasses.add(Integer.TYPE);
							argumentObjects.add(constIndexStack.pop());
						}
					}
				}
				Constructor<?> constructor = instructionClass
						.getConstructor(argumentClasses.toArray(new Class[0]));
				Instruction instruction = (Instruction) constructor
						.newInstance(argumentObjects.toArray(new Object[0]));
				InstructionHandle handle;
				if(instructions.size() > startingIndex + insertedCount) {
					InstructionHandle insertionPoint = instructions
							.getInstructionHandles()[startingIndex
							+ insertedCount];
					if(instruction instanceof BranchInstruction)
						handle = instructions.insert(insertionPoint,
								(BranchInstruction) instruction);
					else
						handle = instructions.insert(insertionPoint,
								instruction);
				} else {
					if(instruction instanceof BranchInstruction)
						handle = instructions
								.append((BranchInstruction) instruction);
					else
						handle = instructions.append(instruction);
				}
				if(needsUpdate)
					toUpdate.put(handle, Integer.valueOf(updateOffset));
				insertedCount++;
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private BranchHandle createForcedBranchHandle(BranchInstruction instruction) {
		try {
			Class<? extends BranchHandle> branchHandleClass = BranchHandle.class;
			Constructor<? extends BranchHandle> constructor = branchHandleClass
					.getConstructor(BranchInstruction.class);
			return constructor.newInstance(instruction);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public void finish() {
		InstructionSearcher searcher = new InstructionSearcher(instructions,
				classGen.getConstantPool());
		for(InstructionHandle handle : toUpdate.keySet()) {
			int offset = toUpdate.get(handle);
			searcher.setCurrent(handle);
			if(offset >= 0)
				for(int i = 0; i < offset; i++)
					searcher.getNext();
			else
				for(int i = 0; i > offset; i--)
					searcher.getPrev();
			((BranchInstruction) handle.getInstruction()).setTarget(searcher
					.getCurrentHandle());
		}
		instructions.setPositions();
		method.setInstructionList(instructions);
		method.setMaxLocals();
		method.setMaxStack();
		String methodName = method.getName();
		String methodSignature = method.getSignature();
		Method oldMethod = null;
		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature()))
				oldMethod = method;
		if(oldMethod != null)
			classGen.replaceMethod(oldMethod, method.getMethod());
		else
			classGen.addMethod(method.getMethod());
		classGen.setConstantPool(method.getConstantPool());
	}

	public ClassGen getClassGen() {
		return classGen;
	}
}
