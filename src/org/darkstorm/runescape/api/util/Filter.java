package org.darkstorm.runescape.api.util;

public interface Filter<T> {
	public boolean accept(T value);
}