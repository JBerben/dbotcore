package org.darkstorm.runescape.oldschool.transformers;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.hooks.BytecodeHook;
import org.darkstorm.bcel.hooks.bytecode.*;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.Event;
import org.darkstorm.runescape.event.internal.CallbackEvent;
import org.darkstorm.runescape.oldschool.OldSchoolBot;
import org.darkstorm.runescape.oldschool.overrides.GameCanvas;

public class CanvasTransformer extends Transformer {

	public CanvasTransformer(Updater updater) {
		super(updater);
	}

	@Override
	public boolean isLocatedIn(ClassGen classGen) {
		return classGen.getSuperclassName().equals(Canvas.class.getName());
	}

	@Override
	public void updateHook(ClassGen classGen) {
		addClass(classGen, GameCanvas.class.getName());
		List<XMLInstruction> instructions = new ArrayList<XMLInstruction>();
		instructions.add(new AddConstInstruction("client", "bot", Type.getType(
				OldSchoolBot.class).getSignature(),
				AddConstInstruction.FIELDREF));
		instructions.add(new BCELInstruction(GETSTATIC.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new AddConstInstruction(OldSchoolBot.class.getName(),
				"getEventManager", "()"
						+ Type.getType(EventManager.class).getSignature(),
				AddConstInstruction.METHODREF));
		instructions.add(new BCELInstruction(INVOKEVIRTUAL.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(ASTORE.class, 1));
		instructions.add(new AddConstInstruction(Type.getType(
				CallbackEvent.class).getSignature(),
				AddConstInstruction.CLASS_OBJECT));
		instructions.add(new BCELInstruction(NEW.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(DUP.class));
		instructions.add(new AddConstInstruction("paint"));
		instructions.add(new BCELInstruction(LDC.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(ICONST.class, 0));
		instructions.add(new AddConstInstruction(Type.OBJECT.getSignature(),
				AddConstInstruction.CLASS_OBJECT));
		instructions.add(new BCELInstruction(ANEWARRAY.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new AddConstInstruction(CallbackEvent.class.getName(),
				"<init>", "(Ljava/lang/String;[Ljava/lang/Object;)V",
				AddConstInstruction.METHODREF));
		instructions.add(new BCELInstruction(INVOKESPECIAL.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(ASTORE.class, 2));

		instructions.add(new BCELInstruction(ALOAD.class, 1));
		instructions.add(new BCELInstruction(ALOAD.class, 2));
		instructions.add(new AddConstInstruction(EventManager.class.getName(),
				"sendEvent", "(" + Type.getType(Event.class).getSignature()
						+ ")V", AddConstInstruction.INTERFACE_METHODREF));
		instructions.add(new BCELInstruction(INVOKEINTERFACE.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT, 2));

		instructions.add(new BCELInstruction(ALOAD.class, 2));
		instructions.add(new AddConstInstruction(CallbackEvent.class.getName(),
				"getReturnObject", "()" + Type.OBJECT.getSignature(),
				AddConstInstruction.METHODREF));
		instructions.add(new BCELInstruction(INVOKEVIRTUAL.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(ASTORE.class, 3));
		instructions.add(new BCELInstruction(ALOAD.class, 3));
		instructions.add(new BCELInstruction(IFNULL.class, 4));
		instructions.add(new BCELInstruction(ALOAD.class, 3));
		instructions.add(new AddConstInstruction(Type.getType(Graphics.class)
				.getSignature(), AddConstInstruction.CLASS_OBJECT));
		instructions.add(new BCELInstruction(CHECKCAST.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(ARETURN.class));
		instructions.add(new BCELInstruction(ALOAD.class, 0));
		instructions.add(new AddConstInstruction(Canvas.class.getName(),
				"getGraphics", "()"
						+ Type.getType(Graphics.class).getSignature(),
				AddConstInstruction.METHODREF));
		instructions.add(new BCELInstruction(INVOKESPECIAL.class,
				BCELInstruction.NEW_CONSTANT_ARGUMENT));
		instructions.add(new BCELInstruction(ARETURN.class));
		hooks.add(new BytecodeHook(classGen.getClassName(), "getGraphics", "()"
				+ Type.getType(Graphics.class).getSignature(), 0, instructions
				.toArray(new XMLInstruction[instructions.size()])));
	}
}
