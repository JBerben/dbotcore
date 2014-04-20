package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.darkstorm.bcel.hooks.bytecode.XMLInstruction;
import org.jdom.Element;

public class NewMethodHook extends Hook {
	private String className, interfaceName, methodName, methodSignature;
	private XMLInstruction[] instructions;
	private Element[] instructionElements;

	public NewMethodHook(Element element) {
		super(element);
	}

	public NewMethodHook(String className, String interfaceName,
			String methodName, String methodSignature,
			XMLInstruction[] instructions) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
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
		return interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public XMLInstruction[] getInstructions() {
		return instructions;
	}

	@Override
	public Element toXML() {
		Element element = new Element("method");
		element.setAttribute("class", className);
		element.setAttribute("method", methodName);
		element.setAttribute("signature", methodSignature);
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
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");
		instructionElements = (Element[]) element.getChildren("instruction")
				.toArray(new Element[0]);
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		ClassGen interfaceClass = classGen;

		MethodGen newMethod = new MethodGen(Constants.ACC_PUBLIC,
				Type.getReturnType(methodSignature),
				Type.getArgumentTypes(methodSignature), null, methodName,
				interfaceClass.getClassName(), new InstructionList(),
				interfaceClass.getConstantPool());
		BytecodeHookParser parser = new BytecodeHookParser(classGen, newMethod,
				0);
		for(Element instruction : instructionElements)
			parser.parseNext(instruction);
		parser.finish();
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC
				| Constants.ACC_ABSTRACT, Type.getReturnType(methodSignature),
				Type.getArgumentTypes(methodSignature), null, methodName,
				classGen.getClassName(), null, classGen.getConstantPool());
		classGen.addMethod(method.getMethod());
		return classGen;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof NewMethodHook))
			return false;
		NewMethodHook method = (NewMethodHook) hook;
		return interfaceName.equals(method.getInterfaceName())
				&& methodName.equals(method.getMethodName())
				&& methodSignature.equals(method.getMethodSignature());
	}
}
