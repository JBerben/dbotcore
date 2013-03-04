package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;

public class NPCDefTransformer extends Transformer {

	public NPCDefTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
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
		if(!(hasStringArray && hasString && hasInt && classGen
				.getSuperclassName().equals(
						updater.getClasses().getByInterface(updater, "NodeSub")
								.getClassName())))
			return false;
		ClassGen characterClass = updater.getClasses().getByInterface(updater,
				"Character");
		for(ClassGen otherClass : updater.getClasses())
			if(otherClass.getSuperclassName().equals(
					characterClass.getClassName()))
				for(Field field : otherClass.getFields())
					if(field.getType() instanceof ObjectType
							&& ((ObjectType) field.getType()).getClassName()
									.equals(classGen.getClassName()))
						return true;
		return false;
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "NPCDef");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] { CharacterTransformer.class,
				NodeSubTransformer.class };
	}
}
