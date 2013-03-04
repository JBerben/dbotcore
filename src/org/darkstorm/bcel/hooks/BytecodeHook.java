package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.darkstorm.bcel.hooks.bytecode.XMLInstruction;
import org.jdom.Element;

public class BytecodeHook extends Hook {
	private String className, methodName, methodSignature;
	private int position;
	private XMLInstruction[] instructions;
	private Element[] instructionElements;

	public BytecodeHook(Element element) {
		super(element);
	}

	public BytecodeHook(String className, String methodName,
			String methodSignature, int position, XMLInstruction[] instructions) {
		this.className = className;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.position = position;
		this.instructions = instructions.clone();
		instructionElements = new Element[this.instructions.length];
		for(int i = 0; i < this.instructions.length; i++)
			instructionElements[i] = this.instructions[i].toXML();
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getInterfaceName() {
		return "bytecode";
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public int getPosition() {
		return position;
	}

	public XMLInstruction[] getInstructions() {
		return instructions;
	}

	@Override
	public Element toXML() {
		Element element = new Element("bytecode");
		element.setAttribute("class", className);
		element.setAttribute("method", methodName);
		element.setAttribute("signature", methodSignature);
		element.setAttribute("position", Integer.toString(position));
		for(Element instructionElement : instructionElements)
			element.addContent(instructionElement);
		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		methodName = element.getAttributeValue("method");
		methodSignature = element.getAttributeValue("signature");
		position = Integer.valueOf(element.getAttributeValue("position"));
		instructionElements = (Element[]) element.getChildren("instruction")
				.toArray(new Element[0]);
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		MethodGen targetMethod = null;
		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature()))
				targetMethod = new MethodGen(method, className,
						classGen.getConstantPool());
		if(targetMethod == null) {
			Type returnType = Type.getReturnType(methodSignature);
			Type[] argumentTypes = Type.getArgumentTypes(methodSignature);
			InstructionList instructions = new InstructionList();
			ConstantPoolGen constantPool = classGen.getConstantPool();
			targetMethod = new MethodGen(Constants.ACC_PUBLIC, returnType,
					argumentTypes, null, methodName, className, instructions,
					constantPool);
		}
		BytecodeHookParser parser = new BytecodeHookParser(classGen,
				targetMethod, position
						+ getOffsetAtPosition(targetMethod, position));
		for(Element instruction : instructionElements)
			parser.parseNext(instruction);
		parser.finish();
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		return null;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {// not possibly accurate
		return false;
	}
}
