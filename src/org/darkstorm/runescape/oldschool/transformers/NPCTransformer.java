package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;

public class NPCTransformer extends Transformer {

	public NPCTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		boolean hasNPCDef = false;
		Type npcDefType = new ObjectType(updater.getClasses()
				.getByInterface(updater, "NPCDef").getClassName());
		for(Field field : classGen.getFields()) {
			if(field.getType().equals(npcDefType))
				hasNPCDef = true;
		}
		return hasNPCDef
				&& classGen.getSuperclassName().equals(
						updater.getClasses()
								.getByInterface(updater, "Character")
								.getClassName());
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "NPC", "Character");
		Type npcDefType = new ObjectType(updater.getClasses()
				.getByInterface(updater, "NPCDef").getClassName());
		for(Field field : classGen.getFields())
			if(field.getType().equals(npcDefType))
				addGetter(classGen, "NPC", field, "getDef");
		ClassGen client = updater.getClasses()
				.getByInterface(updater, "Client");
		Type type = new ObjectType(classGen.getClassName());
		for(Field field : client.getFields())
			if(field.getType() instanceof ArrayType
					&& ((ArrayType) field.getType()).getBasicType()
							.equals(type))
				addGetter(client, "Client", field, "getNPCs");
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
		return new Class[] { NPCDefTransformer.class,
				CharacterTransformer.class, AnimableTransformer.class };
	}
}
