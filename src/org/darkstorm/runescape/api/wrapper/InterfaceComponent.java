package org.darkstorm.runescape.api.wrapper;

import java.awt.*;

import org.darkstorm.runescape.api.input.MouseTargetable;
import org.darkstorm.runescape.api.util.*;

public interface InterfaceComponent extends Identifiable, Displayable,
		MouseTargetable, Wrapper {
	public InterfaceComponent[] getChildren();

	public InterfaceComponent getChild(int id);

	public InterfaceComponent getChild(Filter<InterfaceComponent> filter);

	public int[] getItemIds();

	public int[] getItemStackSizes();

	public boolean hasParent();

	public InterfaceComponent getParent();

	public Interface getInterface();

	public Rectangle getRelativeBounds();

	public Rectangle getBounds();

	public Point getCenter();

	public String getText();

	public String getTooltip();

	public String[] getActions();

	public String getSelectedAction();

	public int getTextColor();

	public int getContainedItemId();

	public int getContainedItemStackSize();

	public boolean isInventory();

	public int getTextureId();

	public int getScrollPosition();

	public int getScrollHeight();

	public int getModelId();

	public int getModelType();

	public int getType();

	public boolean isValid();
}
