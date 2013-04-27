package org.darkstorm.runescape.script;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.util.CustomClassLoader;

import com.sun.org.apache.bcel.internal.classfile.*;

public class ScriptManagerImpl implements ScriptManager {
	private final Bot bot;
	private final Map<Script, ClassLoader> scripts;
	private final Map<String, Script> scriptsByName;

	public ScriptManagerImpl(Bot bot) {
		this.bot = bot;
		scripts = new ConcurrentHashMap<Script, ClassLoader>();
		scriptsByName = new HashMap<String, Script>();
	}

	@Override
	public void loadScripts() {
	}

	@Override
	public Script[] loadScripts(URL url) throws ScriptLoadException,
			IOException {
		File file = new File(url.getPath());
		if(file != null && file.isDirectory()) {
			CustomClassLoader classLoader = new CustomClassLoader();
			classLoader.addURL(url);
			List<Script> scripts = new ArrayList<Script>();
			for(File subfile : file.listFiles()) {
				if(subfile.isDirectory())
					continue;
				if(subfile.getName().endsWith(".class")) {
					String className = subfile.getName().substring(0,
							subfile.getName().lastIndexOf(".class"));
					try {
						Class<?> c = classLoader.loadClass(className);
						Script script = defineScript(c);
						scripts.add(script);
					} catch(Exception exception1) {}
				}
			}
			classLoader.close();
			return scripts.toArray(new Script[scripts.size()]);
		}
		return loadScripts(url.openStream());
	}

	@Override
	public Script[] loadScripts(InputStream in) throws ScriptLoadException,
			IOException {
		ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1)
			arrayOut.write(buffer, 0, read);
		byte[] scriptData = arrayOut.toByteArray();
		List<Script> scripts = new ArrayList<Script>();
		CustomClassLoader classLoader = new CustomClassLoader();
		try {
			ClassParser parser = new ClassParser(new ByteArrayInputStream(
					scriptData), "unknown");
			JavaClass javaClass = parser.parse();
			String className = javaClass.getClassName();
			classLoader.addClass(className, scriptData);
			try {
				Class<?> c = classLoader.loadClass(className);
				Script script = defineScript(c);
				scripts.add(script);
			} catch(ClassNotFoundException exception) {
				classLoader.close();
				throw new ScriptLoadException(exception);
			}
		} catch(ClassFormatException exception) {
			JarInputStream inputStream = new JarInputStream(
					new ByteArrayInputStream(scriptData));
			JarEntry entry;
			List<String> classNames = new ArrayList<String>();
			while((entry = inputStream.getNextJarEntry()) != null) {
				String entryName = entry.getName();
				ByteArrayOutputStream dataArray = new ByteArrayOutputStream();
				byte[] dataBuffer = new byte[1024];
				int dataRead;
				while((dataRead = inputStream.read(dataBuffer)) != -1)
					dataArray.write(dataBuffer, 0, dataRead);
				if(entryName.endsWith(".class")) {
					String className = entryName.replace('/', '.').substring(0,
							entryName.lastIndexOf(".class"));
					classLoader.addClass(className, dataArray.toByteArray());
					if(!className.contains("$"))
						classNames.add(className);
				} else
					classLoader.addResource(entryName, dataArray.toByteArray());
			}
			inputStream.close();
			for(String className : classNames)
				try {
					Class<?> c = classLoader.loadClass(className);
					Script script = defineScript(c);
					scripts.add(script);
				} catch(Exception exception1) {
					exception1.printStackTrace();
				}
		}
		classLoader.close();
		synchronized(this.scripts) {
			for(Script script : scripts) {
				this.scripts.put(script, classLoader);
				scriptsByName.put(script.getManifest().name(), script);
			}
		}
		return scripts.toArray(new Script[scripts.size()]);
	}

	@Override
	public Script loadScript(Class<? extends Script> scriptClass)
			throws ScriptLoadException {
		return defineScript(scriptClass);
	}

	private Script defineScript(Class<?> c) throws ScriptLoadException {
		if((c.getModifiers() & Modifier.ABSTRACT) != Modifier.ABSTRACT
				&& Script.class.isAssignableFrom(c)
				&& c.getAnnotation(ScriptManifest.class) != null)
			try {
				Constructor<?> constructor = c
						.getConstructor(ScriptManager.class);
				Script script = (Script) constructor.newInstance(this);
				return script;
			} catch(Exception exception) {
				throw new ScriptLoadException("Unable to instantiate script.",
						exception);
			}
		else
			throw new ScriptLoadException("Improper script class definition.");
	}

	@Override
	public void loadScript(Script script) throws ScriptLoadException {
		if(script == null)
			throw new NullPointerException();
		synchronized(scripts) {
			scripts.put(script, script.getClass().getClassLoader());
			scriptsByName.put(script.getManifest().name(), script);
		}
	}

	@Override
	public void unloadScripts() {
		synchronized(scripts) {
			for(Script script : scripts.keySet())
				try {
					ClassLoader classLoader = scripts.get(script);
					if(classLoader instanceof CustomClassLoader)
						((CustomClassLoader) classLoader).clearData();
				} catch(Throwable exception) {}
			scripts.clear();
			scriptsByName.clear();
		}
	}

	@Override
	public void unloadScript(Script script) {
		synchronized(scripts) {
			try {
				ClassLoader classLoader = scripts.get(script);
				if(classLoader instanceof CustomClassLoader)
					((CustomClassLoader) classLoader).clearData();
			} catch(Throwable exception) {}
			scripts.remove(script);
			scriptsByName.remove(script.getManifest().name());
		}
	}

	@Override
	public Script getScript(String name) {
		if(name == null)
			throw new NullPointerException();
		synchronized(scripts) {
			return scriptsByName.get(name);
		}
	}

	@Override
	public Script[] getLoadedScripts() {
		synchronized(scripts) {
			return scripts.keySet().toArray(new Script[scripts.size()]);
		}
	}

	@Override
	public Script[] getActiveScripts() {
		List<Script> activeScripts = new ArrayList<Script>();
		synchronized(scripts) {
			for(Script script : scripts.keySet())
				if(script.isActive())
					activeScripts.add(script);
		}
		return activeScripts.toArray(new Script[activeScripts.size()]);
	}

	@Override
	public Bot getBot() {
		return bot;
	}
}
