package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.util.*;

public interface Character extends Animable, Nameable, Rotatable {
	public Tile[] getWaypoints();

	public String getOverheadMessage();

	public int getAnimation();

	public boolean isInCombat();

	public int getHealth();

	public int getMaxHealth();

	public int getHealthPercentage();

	public boolean isDead();

	public int getMotion();

	public boolean isMoving();

	public Character getInteractionTarget();

	public int getLevel();
}
