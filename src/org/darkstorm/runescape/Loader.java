package org.darkstorm.runescape;

import java.applet.Applet;
import java.io.IOException;

import org.darkstorm.runescape.util.*;

public interface Loader {
	public void load(Cache cache, Status status) throws IOException;

	public ClassLoader getClassLoader();

	public Applet createApplet(Cache cache, Status status);
}
