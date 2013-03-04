package org.darkstorm.runescape.oldschool.transformers;

import java.awt.event.*;

import org.apache.bcel.generic.ClassGen;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.runescape.oldschool.overrides.Mouse;

public class MouseTransformer extends Transformer {

	public MouseTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		boolean mouse = false, mouseMotion = false, focus = false;
		for(String iface : classGen.getInterfaceNames()) {
			if(iface.equals(MouseListener.class.getName()))
				mouse = true;
			else if(iface.equals(MouseMotionListener.class.getName()))
				mouseMotion = true;
			else if(iface.equals(FocusListener.class.getName()))
				focus = true;
		}
		return mouse && mouseMotion && focus;
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addClass(classGen, Mouse.class.getName());
		// addInterface(classGen, "Mouse", MouseListener.class.getName(),
		// MouseMotionListener.class.getName(),
		// FocusListener.class.getName());
		// InstructionSearcher searcher = new InstructionSearcher(classGen,
		// getMethod(classGen, "mouseMoved"));
		// searcher.getNext(LDC.class);
	}

}
