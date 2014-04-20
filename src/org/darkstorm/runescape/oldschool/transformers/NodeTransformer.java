package org.darkstorm.runescape.oldschool.transformers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.*;
import org.darkstorm.bcel.transformers.Transformer;

public class NodeTransformer extends Transformer {

	public NodeTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
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
		addInterface(classGen, "Node");
		ObjectType classType = new ObjectType(classGen.getClassName());
		for(Field field : classGen.getFields())
			if(field.getType().equals(Type.LONG))
				addGetter(classGen, "Node", field, "getID");
			else if(classType.equals(field.getType()))
				if(field.isPublic())
					addGetter(classGen, "Node", field, "getPrevious");
				else
					addGetter(classGen, "Node", field, "getNext");
	}
}
