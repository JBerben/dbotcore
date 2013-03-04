package org.darkstorm.bcel;

import java.util.*;
import java.util.jar.*;

import java.io.*;

import org.apache.bcel.generic.ClassGen;
import org.darkstorm.bcel.hooks.*;
import org.darkstorm.runescape.util.Status;

public class ClassGenerator {
	private final Hook[] hooks;
	private ClassGen[] classes;

	public ClassGenerator(Hook[] hooks) {
		this.hooks = hooks.clone();
	}

	public void generateClasses(Status status) {
		String message = status.getMessage();
		status.setProgress(0);
		int progress = 0;
		Map<String, ClassGen> classes = new HashMap<String, ClassGen>();
		for(Hook hook : hooks) {
			if(hook instanceof InterfaceHook) {
				classes.put(hook.getInterfaceName(),
						hook.generateInterface(null));
				status.setProgress((int) ((++progress / (double) hooks.length) * 100));
				status.setMessage(message + " - " + progress + "/"
						+ hooks.length + " (" + status.getProgress() + "%)");
			}
		}
		for(Hook hook : hooks) {
			if(!(hook instanceof InterfaceHook)) {
				ClassGen classGen = hook.generateInterface(classes.get(hook
						.getInterfaceName()));
				if(classGen != null)
					classes.put(hook.getInterfaceName(), classGen);
				status.setProgress((int) ((++progress / (double) hooks.length) * 100));
				status.setMessage(message + " - " + progress + "/"
						+ hooks.length + " (" + status.getProgress() + "%)");
			}
		}
		status.setProgress(100);
		ClassGen[] classArray = new ClassGen[classes.size()];
		classArray = classes.values().toArray(classArray);
		this.classes = classArray;
	}

	public ClassGen[] getClasses() {
		return classes.clone();
	}

	public void dumpJar(File file) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);
		JarOutputStream out = new JarOutputStream(stream);
		for(ClassGen classGen : classes) {
			if(classGen == null)
				continue;
			JarEntry jarEntry = new JarEntry(classGen.getClassName().replace(
					'.', '/')
					+ ".class");
			out.putNextEntry(jarEntry);
			out.write(classGen.getJavaClass().getBytes());
		}
		out.close();
		stream.close();
	}
}
