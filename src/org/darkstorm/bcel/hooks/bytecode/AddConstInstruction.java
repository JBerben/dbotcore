package org.darkstorm.bcel.hooks.bytecode;

import org.jdom.Element;

public class AddConstInstruction extends XMLInstruction {
	public static final int FIELDREF = 0;
	public static final int METHODREF = 1;
	public static final int INTERFACE_METHODREF = 2;

	public static final int CLASS_OBJECT = 0;
	public static final int CLASS_ARRAY = 1;

	private String type;
	private Object value;

	public AddConstInstruction(String constant) {
		type = "String";
		value = constant;
	}

	public AddConstInstruction(int constant) {
		type = "Integer";
		value = constant;
	}

	public AddConstInstruction(float constant) {
		type = "Float";
		value = constant;
	}

	public AddConstInstruction(double constant) {
		type = "Double";
		value = constant;
	}

	public AddConstInstruction(long constant) {
		type = "Long";
		value = constant;
	}

	public AddConstInstruction(String classSignature, int classType) {
		switch(classType) {
		case CLASS_OBJECT:
			type = "Class";
			break;
		case CLASS_ARRAY:
			type = "ArrayClass";
		}
		value = classSignature;
	}

	public AddConstInstruction(String className, String name, String signature,
			int refType) {
		switch(refType) {
		case FIELDREF:
			type = "Fieldref";
			break;
		case METHODREF:
			type = "Methodref";
			break;
		case INTERFACE_METHODREF:
			type = "InterfaceMethodref";
		}
		value = className + ":" + name + ":" + signature;
	}

	@Override
	public Element toXML() {
		Element element = new Element("instruction");
		element.setAttribute("type", "addconst");
		element.setAttribute("const", type);
		element.setAttribute("arguments", value.toString());
		return element;
	}

}
