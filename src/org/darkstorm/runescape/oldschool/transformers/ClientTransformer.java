package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.hooks.FieldHook;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.runescape.oldschool.OldSchoolBot;

public class ClientTransformer extends Transformer {

	public ClientTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		return classGen.getClassName().equals("client");
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "Client");
		hooks.add(new FieldHook(classGen.getClassName(), updater
				.getHooksPackage() + "Client", "bot", Type.getType(
				OldSchoolBot.class).getSignature(), true, OldSchoolBot.class
				.getName(), "getBot", "setBot"));
	}
}
