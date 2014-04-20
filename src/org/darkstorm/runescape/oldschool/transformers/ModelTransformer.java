package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;

public class ModelTransformer extends Transformer {

	public ModelTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		if(!possibleModel(classGen))
			return false;
		ClassGen playerClass = updater.getClasses().getByInterface(updater,
				"Player");
		for(Field field : playerClass.getFields())
			if(field.getType() instanceof ObjectType
					&& updater.getClasses().getByName(
							((ObjectType) field.getType()).getClassName()) != null
					&& updater
							.getClasses()
							.getByName(
									((ObjectType) field.getType())
											.getClassName()).equals(classGen))
				return true;
		return false;
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

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "Model", "Animable");
		ClassGen animableClass = updater.getClasses().getByInterface(updater,
				"Animable");
		Type type = new ObjectType(classGen.getClassName());
		int i = 0;
		for(Method method : animableClass.getMethods())
			if(method.getArgumentTypes().length == 0
					&& method.getReturnType().equals(type))
				addMethod(animableClass, "Animable", method, "getModel" + i++);
		ClassGen playerClass = updater.getClasses().getByInterface(updater,
				"Player");
		for(Field field : playerClass.getFields())
			if(field.getType().equals(type))
				addGetter(playerClass, "Player", field, "getModel");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] { PlayerTransformer.class };
	}
}
