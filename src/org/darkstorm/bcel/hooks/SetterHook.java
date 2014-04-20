package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.jdom.Element;

public class SetterHook extends Hook {
	private String className, interfaceName, fieldName, fieldSignature,
			argumentType, setterName;
	private boolean isStatic;

	public SetterHook(Element element) {
		super(element);
	}

	public SetterHook(String className, String interfaceName, String fieldName,
			String fieldSignature, boolean isStatic, String returnType,
			String setterName) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.fieldName = fieldName;
		this.fieldSignature = fieldSignature;
		this.isStatic = isStatic;
		argumentType = returnType;
		this.setterName = setterName;
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

	public String getFieldSignature() {
		return fieldSignature;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public String getArgumentType() {
		return argumentType;
	}

	public String getSetterName() {
		return setterName;
	}

	@Override
	public Element toXML() {
		Element element = new Element("setter");
		element.setAttribute("class", className);
		element.setAttribute("field", fieldName);
		element.setAttribute("signature", fieldSignature);
		element.setAttribute("static", Boolean.toString(isStatic));
		element.setAttribute("argument", argumentType);
		element.setAttribute("setter", setterName);
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		fieldName = element.getAttributeValue("field");
		fieldSignature = element.getAttributeValue("signature");
		isStatic = Boolean.valueOf(element.getAttributeValue("static"));
		argumentType = element.getAttributeValue("argument");
		setterName = element.getAttributeValue("setter");
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		InstructionFactory factory = new InstructionFactory(classGen);
		InstructionList instructionList = new InstructionList();
		if(!isStatic)
			instructionList.append(new ALOAD(0));
		Type fieldType = Type.getType(fieldSignature);
		instructionList.append(InstructionFactory.createLoad(fieldType, 1));
		Type returnType = getType(argumentType);
		String returnTypeSignature = returnType.getSignature();
		String fieldTypeSignature = fieldType.getSignature();
		if(!returnTypeSignature.equals(fieldTypeSignature))
			instructionList.append(factory.createCast(returnType, fieldType));
		instructionList.append(factory.createPutField(classGen.getClassName(),
				fieldName, fieldType));
		instructionList.append(new RETURN());
		MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
				new Type[] { returnType }, new String[] { fieldName },
				setterName, className, instructionList,
				classGen.getConstantPool());
		methodGen.setMaxLocals();
		methodGen.setMaxStack();
		classGen.addMethod(methodGen.getMethod());
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC
				| Constants.ACC_ABSTRACT, Type.VOID,
				new Type[] { getType(argumentType) },
				new String[] { fieldName }, setterName,
				classGen.getClassName(), null, classGen.getConstantPool());
		classGen.addMethod(method.getMethod());
		return classGen;
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
		return new ArrayType(baseClassName, dimensions);
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof SetterHook))
			return false;
		SetterHook setter = (SetterHook) hook;
		return interfaceName.equals(setter.getInterfaceName())
				&& argumentType.equals(setter.getArgumentType())
				&& setterName.equals(setter.getSetterName())
				&& isStatic == setter.isStatic();
	}
}
