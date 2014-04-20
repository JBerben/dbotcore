package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.jdom.Element;

public class FieldHook extends Hook {
	private String className, interfaceName, fieldName, fieldSignature,
			interfacedType, getterName, setterName;
	private boolean isStatic;

	public FieldHook(Element element) {
		super(element);
	}

	public FieldHook(String className, String interfaceName, String fieldName,
			String fieldSignature, boolean isStatic, String interfacedType,
			String getterName) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.fieldName = fieldName;
		this.fieldSignature = fieldSignature;
		this.isStatic = isStatic;
		this.interfacedType = interfacedType;
		this.getterName = getterName;
	}

	public FieldHook(String className, String interfaceName, String fieldName,
			String fieldSignature, boolean isStatic, String interfacedType,
			String getterName, String setterName) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.fieldName = fieldName;
		this.fieldSignature = fieldSignature;
		this.isStatic = isStatic;
		this.interfacedType = interfacedType;
		this.getterName = getterName;
		this.setterName = setterName;
	}

	public String getFieldSignature() {
		return fieldSignature;
	}

	public String getReturnType() {
		return interfacedType;
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

	public String getSetterName() {
		return setterName;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public Element toXML() {
		Element element = new Element("field");
		element.setAttribute("class", className);
		element.setAttribute("field", fieldName);
		element.setAttribute("signature", fieldSignature);
		element.setAttribute("static", Boolean.toString(isStatic));
		element.setAttribute("interfaced", interfacedType);
		element.setAttribute("getter", getterName);
		if(setterName != null)
			element.setAttribute("setter", setterName);
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		fieldName = element.getAttributeValue("field");
		fieldSignature = element.getAttributeValue("signature");
		isStatic = Boolean.valueOf(element.getAttributeValue("static"));
		interfacedType = element.getAttributeValue("interfaced");
		getterName = element.getAttributeValue("getter");
		setterName = element.getAttributeValue("setter");
		element = element.getParentElement();
		interfaceName = element.getAttributeValue("interface");
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		short accessFlags = Constants.ACC_PUBLIC;
		if(isStatic)
			accessFlags |= Constants.ACC_STATIC;
		Type fieldType = Type.getType(fieldSignature);
		FieldGen field = new FieldGen(accessFlags, fieldType, fieldName,
				classGen.getConstantPool());
		classGen.addField(field.getField());

		createGetter(injector, classGen);
		if(setterName != null)
			createSetter(injector, classGen);
	}

	private void createGetter(Injector injector, ClassGen classGen) {
		ConstantPoolGen cp = classGen.getConstantPool();
		InstructionList iList = new InstructionList();
		Type returnType = getType(interfacedType);
		MethodGen method = new MethodGen(Constants.ACC_PUBLIC, returnType,
				Type.NO_ARGS, new String[] {}, getterName,
				classGen.getClassName(), iList, cp);
		InstructionFactory iFact = new InstructionFactory(classGen, cp);
		Instruction get;
		if(!isStatic) {
			iList.append(new ALOAD(0));
			get = iFact.createFieldAccess(className, fieldName,
					Type.getType(fieldSignature), Constants.GETFIELD);
		} else
			get = iFact.createFieldAccess(className, fieldName,
					Type.getType(fieldSignature), Constants.GETSTATIC);
		Instruction returner = InstructionFactory.createReturn(returnType);
		iList.append(get);
		if(!fieldSignature.equals(returnType.getSignature()))
			iList.append(iFact.createCast(Type.getType(fieldSignature),
					returnType));
		iList.append(returner);
		method.setMaxStack();
		method.setMaxLocals();
		classGen.addMethod(method.getMethod());
	}

	private void createSetter(Injector injector, ClassGen classGen) {
		InstructionFactory factory = new InstructionFactory(classGen);
		InstructionList instructionList = new InstructionList();
		if(!isStatic)
			instructionList.append(new ALOAD(0));
		Type fieldType = Type.getType(fieldSignature);
		instructionList.append(InstructionFactory.createLoad(fieldType, 1));
		Type returnType = getType(interfacedType);
		String returnTypeSignature = returnType.getSignature();
		String fieldTypeSignature = fieldType.getSignature();
		if(!returnTypeSignature.equals(fieldTypeSignature))
			instructionList.append(factory.createCast(returnType, fieldType));
		instructionList.append(factory.createFieldAccess(classGen
				.getClassName(), fieldName, fieldType,
				isStatic ? Constants.PUTSTATIC : Constants.PUTFIELD));
		instructionList.append(new RETURN());
		MethodGen methodGen = new MethodGen(Constants.ACC_PUBLIC, Type.VOID,
				new Type[] { returnType }, new String[] { fieldName },
				setterName, className, instructionList,
				classGen.getConstantPool());
		methodGen.setMaxLocals();
		methodGen.setMaxStack();
		classGen.addMethod(methodGen.getMethod());
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
				| Constants.ACC_ABSTRACT, getType(interfacedType),
				Type.NO_ARGS, new String[0], getterName,
				classGen.getClassName(), null, classGen.getConstantPool());
		classGen.addMethod(method.getMethod());
		if(setterName != null) {
			method = new MethodGen(Constants.ACC_PUBLIC
					| Constants.ACC_ABSTRACT, Type.VOID,
					new Type[] { getType(interfacedType) },
					new String[] { fieldName }, setterName,
					classGen.getClassName(), null, classGen.getConstantPool());
			classGen.addMethod(method.getMethod());
		}
		return classGen;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof FieldHook))
			return false;
		FieldHook getter = (FieldHook) hook;
		return interfaceName.equals(getter.getInterfaceName())
				&& interfacedType.equals(getter.getReturnType())
				&& getterName.equals(getter.getGetterName())
				&& isStatic == getter.isStatic();
	}
}
