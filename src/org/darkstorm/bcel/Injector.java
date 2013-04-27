package org.darkstorm.bcel;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.deobbers.Deobber;
import org.darkstorm.bcel.hooks.*;
import org.darkstorm.bcel.util.ClassVector;
import org.darkstorm.runescape.util.Status;

public class Injector {
	private final Hook[] hooks;
	private final List<String> entryNames;
	private final ClassVector classes;
	private final List<Deobber> deobbers;

	public Injector(byte[] data, Hook[] hooks) {
		this.hooks = hooks.clone();
		classes = new ClassVector();
		entryNames = new ArrayList<String>();
		deobbers = new LinkedList<Deobber>();
		loadJar(data);
	}

	private void loadJar(byte[] data) {
		try {
			JarInputStream in = new JarInputStream(new ByteArrayInputStream(
					data));
			JarEntry entry;
			while((entry = in.getNextJarEntry()) != null) {
				entryNames.add(entry.getName());
				if(entry.getName().endsWith(".class")) {
					ClassParser entryClassParser = new ClassParser(in,
							entry.getName());
					JavaClass parsedClass = entryClassParser.parse();
					ClassGen classGen = new ClassGen(parsedClass);
					classes.add(classGen);
				}
			}
			in.close();
		} catch(RuntimeException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void registerDeobber(Deobber deobber) {
		deobbers.add(deobber);
	}

	public void deobfuscate(Status status) {
		String message = status.getMessage();
		int max = deobbers.size() * classes.size();
		int progress = 0;
		int deobberIndex = 1;
		status.setProgress((int) ((progress / (double) max) * 100));
		status.setMessage(message + " - " + deobberIndex + "/"
				+ deobbers.size() + " (" + status.getProgress() + "%)");
		for(Deobber deobber : deobbers) {
			for(ClassGen classGen : classes) {
				deobber.deob(classGen);
				progress++;
				status.setProgress((int) ((progress / (double) max) * 100));
				status.setMessage(message + " - " + deobberIndex + "/"
						+ deobbers.size() + " (" + status.getProgress() + "%)");
			}
			deobber.finish();
			deobberIndex++;
		}
		deobbers.clear();
		status.setProgress(100);
	}

	public void injectHooks(Status status) {
		String message = status.getMessage();
		status.setProgress(0);
		List<Hook> injected = new ArrayList<Hook>();
		for(ClassGen classGen : classes) {
			for(Hook hook : hooks) {
				if(hook instanceof InterfaceHook) {
					if(!injected.contains(hook)) {
						try {
							if(hook.isInjectable(this, classGen)) {
								hook.inject(this, classGen);
								if(!injected.contains(hook))
									injected.add(hook);
								status.setProgress((int) ((injected.size() / (double) hooks.length) * 100));
								status.setMessage(message + " - "
										+ injected.size() + "/" + hooks.length
										+ " (" + status.getProgress() + "%)");
							}
						} catch(Exception exception) {
							exception.printStackTrace();
							printFailedHook(hook);
						}
					}
				}
			}
		}
		List<Hook> hooks = new ArrayList<Hook>();
		for(Hook hook : this.hooks) {
			if(!(hook instanceof InterfaceHook))
				hooks.add(hook);
		}
		for(ClassGen classGen : classes) {
			for(Hook hook : hooks) {
				try {
					if(hook.isInjectable(this, classGen)) {
						hook.inject(this, classGen);
						if(!injected.contains(hook))
							injected.add(hook);

						status.setProgress((int) ((injected.size() / (double) this.hooks.length) * 100));
						status.setMessage(message + " - " + injected.size()
								+ "/" + this.hooks.length + " ("
								+ status.getProgress() + "%)");
					}
				} catch(Exception exception) {
					exception.printStackTrace();
					printFailedHook(hook);
				}
			}
		}
		if(injected.size() < this.hooks.length) {
			System.err.println("Failure to inject all hooks. Hooks injected: "
					+ injected.size() + ", hook count: " + this.hooks.length);
		}
	}

	private void printFailedHook(Hook hook) {
		String message = "Failed to inject ";
		if(hook instanceof InterfaceHook) {
			InterfaceHook interfaceHook = (InterfaceHook) hook;
			message += "interface " + interfaceHook.getInterfaceName();
		} else if(hook instanceof GetterHook) {
			GetterHook getterHook = (GetterHook) hook;
			message += "getter " + getterHook.getReturnType() + " "
					+ getterHook.getInterfaceName() + "."
					+ getterHook.getGetterName() + "()";
		} else if(hook instanceof SetterHook) {
			SetterHook setterHook = (SetterHook) hook;
			message += "setter " + setterHook.getInterfaceName() + "."
					+ setterHook.getSetterName() + "("
					+ setterHook.getArgumentType() + ")";
		} else if(hook instanceof MethodHook) {
			MethodHook methodHook = (MethodHook) hook;
			message += "method "
					+ Type.getReturnType(methodHook.getNewMethodSignature())
					+ " " + methodHook.getInterfaceName() + "."
					+ methodHook.getNewMethodName() + "(";
			Type[] argumentTypes = Type.getArgumentTypes(methodHook
					.getNewMethodSignature());
			if(argumentTypes.length > 0) {
				message += argumentTypes[0].toString();
				for(int i = 1; i < argumentTypes.length; i++)
					message += ", " + argumentTypes[i].toString();
			}
			message += ")";
		} else if(hook instanceof CallbackHook) {
			CallbackHook callbackHook = (CallbackHook) hook;
			message += "callback to " + callbackHook.getInterfaceName() + "."
					+ callbackHook.getCallbackMethod() + "()";
		} else if(hook instanceof BytecodeHook) {
			BytecodeHook bytecodeHook = (BytecodeHook) hook;
			message += "bytecode to "
					+ Type.getReturnType(bytecodeHook.getMethodSignature())
					+ " " + bytecodeHook.getClassName() + "."
					+ bytecodeHook.getMethodName() + "(";
			Type[] argumentTypes = Type.getArgumentTypes(bytecodeHook
					.getMethodSignature());
			if(argumentTypes.length > 0) {
				message += argumentTypes[0].toString();
				for(int i = 1; i < argumentTypes.length; i++)
					message += ", " + argumentTypes[i].toString();
			}
			message += ")";
		} else
			message += "an unknown hook";
		System.err.println(message);
	}

	public void dumpJar(File file) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);
		JarOutputStream out = new JarOutputStream(stream);
		for(ClassGen classGen : classes) {
			JarEntry jarEntry = new JarEntry(classGen.getClassName().replace(
					'.', '/')
					+ ".class");
			out.putNextEntry(jarEntry);
			out.write(classGen.getJavaClass().getBytes());
		}
		out.close();
		stream.close();
	}

	public ClassVector getClasses() {
		return classes;
	}
}