package org.darkstorm.runescape.oldschool.transformers;

import java.awt.event.*;

import org.apache.bcel.generic.ClassGen;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.runescape.oldschool.overrides.Keyboard;

public class KeyboardTransformer extends Transformer {

	public KeyboardTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		boolean key = false, focus = false;
		for(String iface : classGen.getInterfaceNames()) {
			if(iface.equals(KeyListener.class.getName()))
				key = true;
			else if(iface.equals(FocusListener.class.getName()))
				focus = true;
		}
		return key && focus;
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addClass(classGen, Keyboard.class.getName());
		// addInterface(classGen, "Keyboard", KeyListener.class.getName(),
		// FocusListener.class.getName());
		// InstructionSearcher searcher = new InstructionSearcher(classGen,
		// getMethod(classGen, "mouseMoved"));
		// searcher.getNext(LDC.class);
	}

}
