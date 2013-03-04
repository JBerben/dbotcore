package org.darkstorm.runescape.oldschool.overrides;

import java.awt.event.*;

public abstract class Keyboard implements KeyListener, FocusListener {
	public void dispatchEvent(KeyEvent keyEvent) {
		switch(keyEvent.getID()) {
		case KeyEvent.KEY_PRESSED:
			keyPressed(keyEvent);
			break;
		case KeyEvent.KEY_TYPED:
			keyTyped(keyEvent);
			break;
		case KeyEvent.KEY_RELEASED:
			keyReleased(keyEvent);
		}
	}
}
