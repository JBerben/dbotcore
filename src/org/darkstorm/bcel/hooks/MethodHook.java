package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.darkstorm.bcel.util.ClassVector;
import org.jdom.Element;

public class MethodHook extends Hook {
	private String className, interfaceName, methodName, methodSignature,
			newMethodName, newMethodSignature;

	public MethodHook(Element element) {
		super(element);
	}

	public MethodHook(String className, String interfaceName,
			String methodName, String methodSignature, String newMethodName,
			String newMethodSignature) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.newMethodName = newMethodName;
		this.newMethodSignature = newMethodSignature;
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

	public String getNewMethodName() {
		return newMethodName;
	}

	public String getNewMethodSignature() {
		return newMethodSignature;
	}

	@Override
	public Element toXML() {
		Element element = new Element("method");
		element.setAttribute("class", className);
		element.setAttribute("method", methodName);
		element.setAttribute("signature", methodSignature);
		element.setAttribute("new_method", newMethodName);
		element.setAttribute("new_signature", newMethodSignature);
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		methodName = element.getAttributeValue("method");
		methodSignature = element.getAttributeValue("signature");
		newMethodName = element.getAttributeValue("new_method");
		newMethodSignature = element.getAttributeValue("new_signature");
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		Method methodToCopy = null;
		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature()))
				methodToCopy = method;
		if(methodToCopy == null)
			return;
		ClassGen interfaceClass = classGen;
		if(methodToCopy.isStatic()) {
			ClassVector classes = injector.getClasses();
			interfaceClass = classes.getByInterface(interfaceName);
		}

		MethodGen newMethod = new MethodGen(Constants.ACC_PUBLIC,
				Type.getReturnType(newMethodSignature),
				Type.getArgumentTypes(newMethodSignature), null, newMethodName,
				interfaceClass.getClassName(), new InstructionList(),
				interfaceClass.getConstantPool());
		InstructionList instructionList = generateCallerMethodBody(
				interfaceClass, methodToCopy, newMethod);
		newMethod.setMaxLocals();
		newMethod.setInstructionList(instructionList);
		newMethod.setMaxLocals();
		newMethod.setMaxStack();
		interfaceClass.addMethod(newMethod.getMethod());
	}

	private InstructionList generateCallerMethodBody(ClassGen classGen,
			Method method, MethodGen newMethod) {
		InstructionList instructionList = new InstructionList();
		InstructionFactory factory = new InstructionFactory(classGen);
		instructionList.append(new ALOAD(0));
		int index = 1;
		for(int i = 0; i < method.getArgumentTypes().length; i++) {
			Type argumentType = newMethod.getArgumentTypes()[i];
			instructionList.append(InstructionFactory.createLoad(argumentType,
					index));
			if(!argumentType.getSignature().equals(
					method.getArgumentTypes()[i].getSignature()))
				instructionList.append(factory.createCast(argumentType,
						method.getArgumentTypes()[i]));
			int indexIncrement = (argumentType == Type.LONG || argumentType == Type.DOUBLE) ? 2
					: 1;
			index += indexIncrement;
		}

		short invokeType = Constants.INVOKEVIRTUAL;
		String methodName = method.getName();
		if(methodName.equals("<init>") || methodName.equals("<clinit>"))
			invokeType = Constants.INVOKESPECIAL;
		else if(method.isStatic())
			invokeType = Constants.INVOKESTATIC;
		instructionList.append(factory.createInvoke(className,
				method.getName(), method.getReturnType(),
				method.getArgumentTypes(), invokeType));
		instructionList.append(InstructionFactory.createReturn(method
				.getReturnType()));
		classGen.setConstantPool(factory.getConstantPool());
		return instructionList;
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC
				| Constants.ACC_ABSTRACT,
				Type.getReturnType(newMethodSignature),
				Type.getArgumentTypes(newMethodSignature), null, newMethodName,
				classGen.getClassName(), null, classGen.getConstantPool());
		classGen.addMethod(method.getMethod());
		return classGen;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof MethodHook))
			return false;
		MethodHook method = (MethodHook) hook;
		return interfaceName.equals(method.getInterfaceName())
				&& newMethodName.equals(method.getNewMethodName())
				&& newMethodSignature.equals(method.getNewMethodSignature());
	}
}
