package org.darkstorm.bcel.hooks;

public interface HookLoader {
	public void load();

	public Hook[] getHooks();

	public String getDataVersion();
}
