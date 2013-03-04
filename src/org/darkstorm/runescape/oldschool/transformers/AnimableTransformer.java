package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;

public class AnimableTransformer extends Transformer {

	public AnimableTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		boolean hasInt = false;
		for(Field field : classGen.getFields())
			if(field.getType().equals(Type.INT))
				hasInt = true;
		return hasInt
				&& updater.getClasses().getByInterface(updater, "Character")
						.getSuperclassName().equals(classGen.getClassName())
				&& classGen.getSuperclassName().equals(
						updater.getClasses().getByInterface(updater, "NodeSub")
								.getClassName());
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "Animable", "NodeSub");
		for(Field field : classGen.getFields())
			if(field.getType().equals(Type.INT) && !field.isStatic())
				addGetter(classGen, "Animable", field, "getHeight");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] { CharacterTransformer.class,
				NodeSubTransformer.class };
	}
}
