package org.darkstorm.runescape.oldschool.transformers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.bcel.util.InstructionSearcher;

public class InterfaceTransformer extends Transformer {

	public InterfaceTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		boolean hasSelfArray = false, hasSelfDoubleArray = false;;
		int objectArrayCount = 0;
		ObjectType classType = new ObjectType(classGen.getClassName());
		for(Field field : classGen.getFields()) {
			Type type = field.getType();
			if(type instanceof ArrayType) {
				ArrayType array = (ArrayType) type;
				if(array.getDimensions() == 1) {
					if(array.getBasicType().equals(classType))
						hasSelfArray = true;
					else if(array.getBasicType().equals(Type.OBJECT))
						objectArrayCount++;
				} else if(field.isStatic() && array.getDimensions() == 2
						&& array.getBasicType().equals(classType))
					hasSelfDoubleArray = true;
			}
		}
		if(hasSelfArray && hasSelfDoubleArray && objectArrayCount > 20)
			return true;
		return false;
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "Interface", "Node");
		ObjectType classType = new ObjectType(classGen.getClassName());
		for(Field field : classGen.getFields()) {
			Type type = field.getType();
			if(type instanceof ArrayType) {
				ArrayType array = (ArrayType) type;
				if(array.getDimensions() == 1) {
					if(array.getBasicType().equals(classType))
						addGetter(classGen, "Interface", field, "getChildren");
				} else if(field.isStatic() && array.getDimensions() == 2
						&& array.getBasicType().equals(classType))
					addGetter(classGen, "Interface", field, "getInterfaces");
			}
		}
		Map<String, AtomicInteger> fieldPutMap = new HashMap<String, AtomicInteger>();
		for(Method method : classGen.getMethods()) {
			MethodGen methodGen = new MethodGen(method,
					classGen.getClassName(), classGen.getConstantPool());
			if(method.getSignature().equals(
					"(I" + Type.STRING.getSignature() + ")V")) {
				InstructionFinder finder = new InstructionFinder(
						methodGen.getInstructionList());
				if(!finder
						.search("aload getfield ifnull goto aconst_null aload getfield if_acmpeq goto aload")
						.hasNext())
					continue;
				InstructionSearcher searcher = new InstructionSearcher(
						methodGen);
				searcher.resetToEnd();
				FieldInstruction fi = searcher.getPrev(GETFIELD.class);
				addGetter(fi, "Interface", classGen.getConstantPool(),
						"getActions");
			}
			InstructionSearcher searcher = new InstructionSearcher(methodGen);
			PUTFIELD instruction;
			while((instruction = searcher.getNext(PUTFIELD.class)) != null) {
				if(!instruction.getReferenceType(classGen.getConstantPool())
						.equals(classType)
						|| !instruction
								.getFieldType(classGen.getConstantPool())
								.equals(Type.STRING))
					continue;
				AtomicInteger integer = fieldPutMap.get(instruction
						.getFieldName(classGen.getConstantPool()));
				if(integer == null) {
					integer = new AtomicInteger();
					fieldPutMap.put(instruction.getFieldName(classGen
							.getConstantPool()), integer);
				}
				integer.incrementAndGet();
			}
		}
		String fieldHighest = null;
		int highestValue = 0;
		for(String fieldName : fieldPutMap.keySet()) {
			int value = fieldPutMap.get(fieldName).get();
			if(fieldHighest == null || value > highestValue) {
				fieldHighest = fieldName;
				highestValue = value;
			}
		}
		String secondHighest = null;
		for(String fieldName : fieldPutMap.keySet()) {
			if(fieldName.equals(fieldHighest))
				continue;
			int value = fieldPutMap.get(fieldName).get();
			if(secondHighest == null || value > highestValue) {
				secondHighest = fieldName;
				highestValue = value;
			}
		}
		addGetter(classGen, "Interface",
				getFieldByName(classGen, secondHighest), "getText");
		// ClassGen animableClass = updater.getClasses().getByInterface(updater,
		// "Animable");
		// Type type = new ObjectType(classGen.getClassName());
		// int i = 0;
		// for(Method method : animableClass.getMethods())
		// if(method.getArgumentTypes().length == 0
		// && method.getReturnType().equals(type))
		// addMethod(animableClass, "Animable", method, "getModel" + i++);
		// ClassGen playerClass = updater.getClasses().getByInterface(updater,
		// "Player");
		// for(Field field : playerClass.getFields())
		// if(field.getType().equals(type))
		// addGetter(playerClass, "Player", field, "getModel");
	}
}
