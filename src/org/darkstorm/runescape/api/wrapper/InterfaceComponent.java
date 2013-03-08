package org.darkstorm.runescape.api.wrapper;

import java.awt.*;

import org.darkstorm.runescape.api.input.MouseTargetable;
import org.darkstorm.runescape.api.util.*;

public interface InterfaceComponent extends Identifiable, Displayable,
		MouseTargetable, Wrapper {
	public InterfaceComponent[] getChildren();

	public InterfaceComponent getChild(int id);

	public Item[] getItems();

	public boolean hasParent();

	public InterfaceComponent getParent();

	public Interface getInterface();

	public Rectangle getRelativeBounds();

	public Rectangle getBounds();

	public Point getCenter();

	public String getText();

	public String[] getActions();

	public int getTextColor();

	public Item getContainedItem();

	public String getContainedItemName();

	public int getTextureId();

	public int getHorizontalScrollBarSize();

	public int getHorizontalScrollBarThumbPosition();

	public int getHorizontalScrollBarThumbSize();

	public int getVerticalScrollBarSize();

	public int getVerticalScrollBarThumbPosition();

	public int getVerticalScrollBarThumbSize();

	public int getModelId();

	public int getModelType();

	public boolean isValid();
}
