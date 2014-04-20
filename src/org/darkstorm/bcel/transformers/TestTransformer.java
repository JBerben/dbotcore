package org.darkstorm.bcel.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;

public class TestTransformer extends Transformer {

	public TestTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		boolean hasSelfArray = false;
		int objectArrayCount = 0;
		for(Field field : classGen.getFields()) {
			Type type = field.getType();
			if(type instanceof ArrayType) {
				ArrayType array = (ArrayType) type;
				if(array.getDimensions() != 1)
					continue;
				if(array.getBasicType().equals(
						new ObjectType(classGen.getClassName())))
					hasSelfArray = true;
				else if(array.getBasicType().equals(Type.OBJECT))
					objectArrayCount++;
			}
		}
		if(hasSelfArray && objectArrayCount > 3) {
			System.out.println("Class " + classGen.getClassName() + " has "
					+ objectArrayCount);
		}
		// ClassGen modelClass = updater.getClasses().getByInterface(updater,
		// "Model");
		// Type modelType = new ObjectType(modelClass.getClassName());
		// for(Field field : classGen.getFields()) {
		// if(!field.getType().equals(modelType))
		// continue;
		// System.out.println("Class " + classGen.getClassName()
		// + " has model! " + field.toString());
		// break;
		// }
		// if(possibleNodeSub(classGen))
		// System.out.println("Possible NPC: " + classGen.getClassName()
		// + " (" + classGen.getSuperclassName() + ")");
		// if(possiblePlayer(classGen))
		// return true;
		// if(possibleCharacter(classGen))
		// return true;
		// ConstantPoolGen constantPool = classGen.getConstantPool();
		// for(int i = 0; i < constantPool.getSize(); i++) {
		// Constant constant = constantPool.getConstant(i);
		// if(constant instanceof ConstantString) {
		// // String value = (String) ((ConstantString) constant)
		// // .getConstantValue(constantPool.getConstantPool());
		// // if(!value.matches("[^\\.]+\\.[^\\(]+\\("))
		// // logger.info("Constant in " + classGen.getClassName() + ": "
		// // + value);
		// } else if(constant instanceof ConstantFieldref) {
		// ConstantFieldref fieldref = (ConstantFieldref) constant;
		// if(fieldref.getClass(constantPool.getConstantPool()).equals(
		// "at")
		// && ((ConstantNameAndType) constantPool
		// .getConstant(fieldref.getNameAndTypeIndex()))
		// .getName(constantPool.getConstantPool())
		// .equals("k")) {
		// for(Method method : classGen.getMethods()) {
		// InstructionSearcher searcher = new InstructionSearcher(
		// classGen, method);
		// while(!searcher.isAtEnd()) {
		// Instruction ins = searcher.getNext();
		// if(ins == null)
		// break;
		// if(ins instanceof GETSTATIC) {
		// if(((CPInstruction) ins).getIndex() == i) {
		// logger.info("Mouse reference @ "
		// + classGen.getClassName() + "."
		// + method);
		// break;
		// }
		// }
		// }
		// }
		// break;
		// }
		// }
		// }
		return false;
	}

	public boolean possibleNPC(ClassGen classGen) {
		boolean hasNPCDef = false;
		for(Field field : classGen.getFields()) {
			if(field.getType() instanceof ObjectType
					&& updater.getClasses().getByName(
							((ObjectType) field.getType()).getClassName()) != null
					&& possibleNPCDef(updater.getClasses().getByName(
							((ObjectType) field.getType()).getClassName())))
				hasNPCDef = true;
		}
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return hasNPCDef && possibleCharacter(superClass);
	}

	public boolean possiblePlayer(ClassGen classGen) {
		boolean hasString = false, hasModel = false, hasBoolean = false;
		int intCount = 0;
		for(Field field : classGen.getFields()) {
			if(field.getType() instanceof ObjectType
					&& updater.getClasses().getByName(
							((ObjectType) field.getType()).getClassName()) != null
					&& possibleModel(updater.getClasses().getByName(
							((ObjectType) field.getType()).getClassName())))
				hasModel = true;
			else if(field.getType().equals(Type.STRING))
				hasString = true;
			else if(field.getType().equals(Type.BOOLEAN))
				hasBoolean = true;
			else if(field.getType().equals(Type.INT))
				intCount++;
		}
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return hasString && hasModel && hasBoolean && intCount >= 2
				&& possibleCharacter(superClass);
	}

	public boolean possibleCharacter(ClassGen classGen) {
		boolean hasString = false;
		int intCount = 0, intArrayCount = 0;
		ArrayType intArray = new ArrayType(Type.INT, 1);
		for(Field field : classGen.getFields()) {
			if(field.getType().equals(Type.STRING))
				hasString = true;
			else if(field.getType().equals(Type.INT))
				intCount++;
			else if(field.getType().equals(intArray))
				intArrayCount++;
		}
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return hasString && intCount >= 3 && intArrayCount >= 2
				&& possibleAnimable(superClass);
	}

	public boolean possibleModel(ClassGen classGen) {
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return possibleAnimable(superClass);
	}

	public boolean possibleAnimable(ClassGen classGen) {
		boolean hasInt = false;
		for(Field field : classGen.getFields()) {
			if(field.getType().equals(Type.INT))
				hasInt = true;
		}
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return hasInt && possibleNodeSub(superClass);
	}

	public boolean possibleNPCDef(ClassGen classGen) {
		boolean hasStringArray = false, hasString = false, hasInt = false;
		ArrayType stringArray = new ArrayType(Type.STRING, 1);
		for(Field field : classGen.getFields()) {
			if(field.getType().equals(stringArray))
				hasStringArray = true;
			else if(field.getType().equals(Type.STRING))
				hasString = true;
			else if(field.getType().equals(Type.INT))
				hasInt = true;
		}
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return hasStringArray && hasString && hasInt
				&& possibleNodeSub(superClass);
	}

	public boolean possibleNodeSub(ClassGen classGen) {
		int selfCount = 0;
		ObjectType classType = new ObjectType(classGen.getClassName());
		for(Field field : classGen.getFields()) {
			if(classType.equals(field.getType()))
				selfCount++;
		}
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return selfCount >= 2 && possibleNode(superClass);
	}

	public boolean possibleNode(ClassGen classGen) {
		int selfCount = 0;
		boolean hasLong = false;
		ObjectType classType = new ObjectType(classGen.getClassName());
		for(Field field : classGen.getFields()) {
			if(classType.equals(field.getType()))
				selfCount++;
			else if(field.getType().equals(Type.LONG))
				hasLong = true;
		}
		return selfCount >= 2 && hasLong;
	}

	@Override
	public void updateHook(ClassGen classGen) {
		if(possiblePlayer(classGen)) {
			// addInterface(classGen, "Player", "Character");

			// ClassGen character = getSuperClass()
			// addInterface(getSuperClass(classGen))
		} else if(possibleNPC(classGen)) {

		}
	}

	// private ClassGen getSuperClass(ClassGen classGen) {
	// return classGen.getSuperclassName() != null ? updater.getClasses()
	// .getByName(classGen.getSuperclassName()) : null;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] {};
	}
}
