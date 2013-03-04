package org.darkstorm.runescape.util;

public interface Cache {
	public boolean isCached(String name);

	public byte[] loadCache(String name);

	public void saveCache(String name, byte[] cache);

	public void removeCache(String name);

	public void clearCache();
}
