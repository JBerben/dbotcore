package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.GameObject;

public interface GameObjects extends TypedUtility<GameObject> {
	public GameObject getClosest(Filter<GameObject> filter);

	public GameObject[] getAll(Filter<GameObject> filter);

	public GameObject[] getAll();
}
