package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.*;
import org.darkstorm.bcel.transformers.Transformer;

public class NodeSubTransformer extends Transformer {

	public NodeSubTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		int selfCount = 0;
		ObjectType classType = new ObjectType(classGen.getClassName());
		for(Field field : classGen.getFields()) {
			if(classType.equals(field.getType()))
				selfCount++;
		}
		return selfCount >= 2
				&& classGen.getSuperclassName().equals(
						updater.getClasses().getByInterface(updater, "Node")
								.getClassName());
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addInterface(classGen, "NodeSub", "Node");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[] { NodeTransformer.class };
	}
}
