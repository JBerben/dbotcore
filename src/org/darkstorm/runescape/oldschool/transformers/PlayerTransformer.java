package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;

public class PlayerTransformer extends Transformer {

	public PlayerTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
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
		return hasString
				&& hasModel
				&& hasBoolean
				&& intCount >= 2
				&& classGen.getSuperclassName().equals(
						updater.getClasses()
								.getByInterface(updater, "Character")
								.getClassName());
	}

	@Override
	public void updateHook(ClassGen classGen) {
		System.out.println("Injected into " + classGen.getClassName());
		addInterface(classGen, "Player", "Character");
		ClassGen client = updater.getClasses()
				.getByInterface(updater, "Client");
		Type type = new ObjectType(classGen.getClassName());
		for(Field field : client.getFields())
			if(field.getType() instanceof ArrayType
					&& ((ArrayType) field.getType()).getBasicType()
							.equals(type))
				addGetter(client, "Client", field, "getPlayers");
	}

	public boolean possibleModel(ClassGen classGen) {
		if(classGen.getSuperclassName() == null)
			return false;
		ClassGen superClass = updater.getClasses().getByName(
				classGen.getSuperclassName());
		if(superClass == null)
			return false;
		return classGen.getSuperclassName().equals(
				updater.getClasses().getByInterface(updater, "Animable")
						.getClassName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] { CharacterTransformer.class,
				AnimableTransformer.class };
	}
}
