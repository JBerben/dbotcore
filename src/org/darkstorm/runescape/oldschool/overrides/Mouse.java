package org.darkstorm.runescape.oldschool.overrides;

import java.awt.event.*;

public abstract class Mouse implements MouseListener, MouseMotionListener,
		FocusListener {
	public void dispatchEvent(MouseEvent mouseEvent) {
		switch(mouseEvent.getID()) {
		case MouseEvent.MOUSE_MOVED:
			mouseMoved(mouseEvent);
			break;
		case MouseEvent.MOUSE_CLICKED:
			mouseClicked(mouseEvent);
			break;
		case MouseEvent.MOUSE_PRESSED:
			mousePressed(mouseEvent);
			break;
		case MouseEvent.MOUSE_RELEASED:
			mouseReleased(mouseEvent);
			break;
		case MouseEvent.MOUSE_DRAGGED:
			mouseDragged(mouseEvent);
			break;
		case MouseEvent.MOUSE_ENTERED:
			mouseEntered(mouseEvent);
			break;
		case MouseEvent.MOUSE_EXITED:
			mouseExited(mouseEvent);
		}
	}
}
