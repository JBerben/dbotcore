package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.darkstorm.runescape.oldschool.MMIRepository;
import org.jdom.Element;

public class GetterHook extends Hook {
	private String className, interfaceName, fieldName, fieldSignature,
			returnType, getterName;
	private boolean isStatic;

	public GetterHook(Element element) {
		super(element);
	}

	public GetterHook(String className, String interfaceName, String fieldName,
			String fieldSignature, boolean isStatic, String returnType,
			String getterName) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.fieldName = fieldName;
		this.fieldSignature = fieldSignature;
		this.isStatic = isStatic;
		this.returnType = returnType;
		this.getterName = getterName;
	}

	public String getFieldSignature() {
		return fieldSignature;
	}

	public String getReturnType() {
		return returnType;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getInterfaceName() {
		return interfaceName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getGetterName() {
		return getterName;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public Element toXML() {
		Element element = new Element("getter");
		element.setAttribute("class", className);
		element.setAttribute("field", fieldName);
		element.setAttribute("signature", fieldSignature);
		element.setAttribute("static", Boolean.toString(isStatic));
		element.setAttribute("return", returnType);
		element.setAttribute("getter", getterName);
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		fieldName = element.getAttributeValue("field");
		fieldSignature = element.getAttributeValue("signature");
		isStatic = Boolean.valueOf(element.getAttributeValue("static"));
		returnType = element.getAttributeValue("return");
		getterName = element.getAttributeValue("getter");
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");

	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		ConstantPoolGen cp = classGen.getConstantPool();
		InstructionList list = new InstructionList();
		Type returnType = getType(this.returnType);
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC, returnType,
				Type.NO_ARGS, new String[] {}, getterName,
				classGen.getClassName(), list, cp);
		InstructionFactory factory = new InstructionFactory(classGen, cp);
		if(returnType.equals(Type.INT) || returnType.equals(Type.LONG)) {
			list.append(factory.createConstant(className));
			list.append(factory.createConstant(fieldName));
		}
		if(!isStatic) {
			list.append(new ALOAD(0));
			list.append(factory.createFieldAccess(className, fieldName,
					Type.getType(fieldSignature), Constants.GETFIELD));
		} else
			list.append(factory.createFieldAccess(className, fieldName,
					Type.getType(fieldSignature), Constants.GETSTATIC));if(returnType.equals(Type.INT) || returnType.equals(Type.LONG))
			list.append(factory.createInvoke(MMIRepository.class.getName(),
					"calculateProduct", returnType, new Type[] { Type.STRING,
							Type.STRING, returnType }, Constants.INVOKESTATIC));
		if(!fieldSignature.equals(returnType.getSignature()))
			list.append(factory.createCast(Type.getType(fieldSignature),
					returnType));
		list.append(InstructionFactory.createReturn(returnType));
		method.setMaxStack();
		method.setMaxLocals();
		classGen.addMethod(method.getMethod());
	}

	private Type getType(String className) {
		if(className.endsWith("]"))
			return getArrayType(className);
		else if(className.equals("boolean"))
			return Type.BOOLEAN;
		else if(className.equals("byte"))
			return Type.BYTE;
		else if(className.equals("short"))
			return Type.SHORT;
		else if(className.equals("int"))
			return Type.INT;
		else if(className.equals("long"))
			return Type.LONG;
		else if(className.equals("float"))
			return Type.FLOAT;
		else if(className.equals("double"))
			return Type.DOUBLE;
		else if(className.equals("char"))
			return Type.CHAR;
		return new ObjectType(className);
	}

	private ArrayType getArrayType(String className) {
		String baseClassName = "";
		int dimensions = 0;
		for(char character : className.toCharArray())
			if(character == '[')
				dimensions++;
			else if(character != ']')
				baseClassName += character;
		Type baseClassType = getType(baseClassName);
		return new ArrayType(baseClassType, dimensions);
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC
				| Constants.ACC_ABSTRACT, getType(returnType), Type.NO_ARGS,
				new String[0], getterName, classGen.getClassName(), null,
				classGen.getConstantPool());
		classGen.addMethod(method.getMethod());
		return classGen;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof GetterHook))
			return false;
		GetterHook getter = (GetterHook) hook;
		return interfaceName.equals(getter.getInterfaceName())
				&& returnType.equals(getter.getReturnType())
				&& getterName.equals(getter.getGetterName())
				&& isStatic == getter.isStatic();
	}
}
