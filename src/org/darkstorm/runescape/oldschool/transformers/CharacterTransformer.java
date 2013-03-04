package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.*;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.bcel.util.InstructionSearcher;

public class CharacterTransformer extends Transformer {

	public CharacterTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
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
		return hasInt
				&& classGen.getSuperclassName().equals(
						updater.getClasses().getByInterface(updater, "NodeSub")
								.getClassName());
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "Character", "Animable");
		for(Field field : classGen.getFields())
			if(field.getType().equals(Type.STRING))
				addGetter(classGen, "Character", field, "getChatMessage");
		for(Method method : classGen.getMethods()) {
			if(method.getSignature().equals("(IIZ)V")) {
				InstructionSearcher searcher = new InstructionSearcher(
						classGen, method);
				searcher.resetToEnd();
				addGetter(searcher.getPrev(PUTFIELD.class), "Character",
						classGen.getConstantPool(), "getY");
				addGetter(searcher.getPrev(PUTFIELD.class), "Character",
						classGen.getConstantPool(), "getX");
				searcher.getPrev(IASTORE.class);
				addGetter(searcher.getPrev(GETFIELD.class), "Character",
						classGen.getConstantPool(), "getWaypointsY");
				addGetter(searcher.getPrev(GETFIELD.class), "Character",
						classGen.getConstantPool(), "getWaypointsX");
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] { NodeSubTransformer.class };
	}
}
