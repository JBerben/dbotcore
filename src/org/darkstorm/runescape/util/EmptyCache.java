package org.darkstorm.runescape.util;


public class EmptyCache implements Cache {
	@Override
	public boolean isCached(String name) {
		return false;
	}

	@Override
	public byte[] loadCache(String name) {
		return null;
	}

	@Override
	public void saveCache(String name, byte[] cache) {
	}

	@Override
	public void removeCache(String name) {
	}

	@Override
	public void clearCache() {
	}
}
