package org.darkstorm.bcel.hooks.bytecode;

import java.lang.reflect.Constructor;

import org.apache.bcel.generic.*;
import org.jdom.Element;

public class BCELInstruction extends XMLInstruction {
	public static final int NEW_CONSTANT_ARGUMENT = -2;

	private Class<? extends Instruction> instruction;
	private String[] arguments;

	public BCELInstruction(Class<? extends Instruction> instruction,
			int... arguments) {
		this.instruction = instruction;
		Constructor<?> correctConstructor = null;
		for(Constructor<?> constructor : instruction.getConstructors())
			if(constructor.getParameterTypes().length == arguments.length)
				correctConstructor = constructor;
		if(correctConstructor == null)
			throw new IllegalArgumentException("Invalid amount of arguments");
		this.arguments = new String[arguments.length];
		for(int i = 0; i < arguments.length; i++) {
			Class<?> argument = correctConstructor.getParameterTypes()[i];
			if(argument.isAssignableFrom(Integer.TYPE))
				this.arguments[i] = ""
						+ (arguments[i] == NEW_CONSTANT_ARGUMENT ? "const"
								: arguments[i]);
			else if(argument.isAssignableFrom(InstructionHandle.class))
				this.arguments[i] = "pos" + arguments[i];
		}
	}

	@Override
	public Element toXML() {
		Element element = new Element("instruction");
		String instructionClassName = instruction.getSimpleName();
		element.setAttribute("type", instructionClassName);
		String allArguments = "";
		if(arguments.length > 0) {
			allArguments = arguments[0];
			for(int i = 1; i < arguments.length; i++)
				allArguments += ":" + arguments[i];
		}
		element.setAttribute("arguments", allArguments);
		return element;
	}

}
