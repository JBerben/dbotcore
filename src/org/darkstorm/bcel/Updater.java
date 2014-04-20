package org.darkstorm.bcel;

import java.io.*;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.hooks.*;
import org.darkstorm.bcel.hooks.bytecode.*;
import org.darkstorm.bcel.transformers.Transformer;
import org.darkstorm.bcel.util.ClassVector;
import org.darkstorm.runescape.util.Status;
import org.jdom.*;
import org.jdom.output.*;

/**
 * @author DarkStorm
 */
public final class Updater {
	private final Logger logger;
	private final ClassVector classes = new ClassVector();
	private final List<Transformer> transformers = new Vector<Transformer>();
	private final String hooksPackage;

	private String dataVersion;

	private Map<String, byte[]> entries = new HashMap<String, byte[]>();

	public Updater(Logger logger, String hooksPackage, byte[] data) {
		this.logger = logger;
		this.hooksPackage = hooksPackage;
		try {
			loadClassGens(data);
		} catch(Exception e) {
			e.printStackTrace();
			logger.throwing(getClass().getName(), "<init>", e);
		}
	}

	private void loadClassGens(byte[] data) throws IOException {
		logger.info("Loading classes...");
		JarInputStream in = new JarInputStream(new ByteArrayInputStream(data));
		ZipEntry entry;
		while((entry = in.getNextEntry()) != null) {
			if(entry.getName().endsWith(".class")) {
				try {
					ClassParser entryClassParser = new ClassParser(in,
							entry.getName());
					JavaClass parsedClass = entryClassParser.parse();
					ClassGen classGen = new ClassGen(parsedClass);
					classes.add(classGen);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			} else
				entries.put(entry.getName(), readAll(in));
		}
		logger.info("Loaded " + classes.size() + " classes.");
	}

	private byte[] readAll(InputStream in) throws IOException {
		ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int bytesRead;
		while((bytesRead = in.read(buffer)) != -1)
			byteArrayOut.write(buffer, 0, bytesRead);
		byteArrayOut.close();
		return byteArrayOut.toByteArray();
	}

	public void registerTransformer(Transformer transformer) {
		transformers.add(transformer);
	}

	public void injectTransforms(Status status) {
		String message = status.getMessage();
		status.setProgress(0);
		logger.info("Injecting transforms...");
		List<Transformer> uninjectedTransformers = new ArrayList<Transformer>(
				transformers);
		List<Transformer> removedTransformers = new ArrayList<Transformer>();
		status.setProgress((int) (((transformers.size() - uninjectedTransformers
				.size()) / (double) transformers.size()) * 100));
		status.setMessage(message + " - "
				+ (transformers.size() - uninjectedTransformers.size()) + "/"
				+ transformers.size() + " (" + status.getProgress() + "%)");
		while(uninjectedTransformers.size() > 0) {
			transformLabel: for(Transformer transformer : uninjectedTransformers) {
				if(transformer.getRequiredTransformers().length > 0)
					for(Class<? extends Transformer> requiredTransformer : transformer
							.getRequiredTransformers())
						if(containsType(uninjectedTransformers,
								requiredTransformer))
							continue transformLabel;
				for(int i = 0; i < 2 && !transformer.hasBeenHooked(); i++)
					for(ClassGen classGen : classes)
						transformer.attemptToUpdate(classGen);

				int progress = transformers.size()
						- uninjectedTransformers.size();
				status.setProgress((int) ((progress / (double) transformers
						.size()) * 100));
				status.setMessage(message + " - " + progress + "/"
						+ transformers.size() + " (" + status.getProgress()
						+ "%)");

				transformer.checkForFails();
				removedTransformers.add(transformer);
			}
			for(Transformer transformer : removedTransformers)
				if(uninjectedTransformers.contains(transformer))
					uninjectedTransformers.remove(transformer);
		}
		status.setProgress(100);
		logger.info("Injected transforms.");
		logger.info("");
	}

	public void printHooks() {
		Vector<InterfaceHook> interfaceHooks = new Vector<InterfaceHook>();
		Vector<Hook> allOtherHooks = new Vector<Hook>();
		for(Transformer transformer : transformers)
			for(Hook hook : transformer.getHooks())
				if(hook instanceof InterfaceHook)
					interfaceHooks.add((InterfaceHook) hook);
				else
					allOtherHooks.add(hook);
		for(InterfaceHook interfaceHook : interfaceHooks) {
			String interfaceName = interfaceHook.getInterfaceName();
			String displayInterfaceName = interfaceName;
			if(interfaceName.startsWith(hooksPackage))
				displayInterfaceName = interfaceName.substring(hooksPackage
						.length());
			String className = interfaceHook.getClassName();
			logger.info("\t+ " + displayInterfaceName + " is implemented by "
					+ className);
			for(Hook hook : allOtherHooks) {
				if(interfaceName.equals(hook.getInterfaceName()))
					if(hook instanceof GetterHook)
						logger.info("\t"
								+ createGetterMessage((GetterHook) hook));
					else if(hook instanceof SetterHook)
						logger.info("\t"
								+ createSetterMessage((SetterHook) hook));
					else if(hook instanceof MethodHook)
						logger.info("\t"
								+ createMethodMessage((MethodHook) hook));
			}
		}
		for(Hook hook : allOtherHooks)
			if(hook instanceof CallbackHook)
				logger.info("\t" + createCallbackMessage((CallbackHook) hook));
			else if(hook instanceof BytecodeHook)
				logger.info("\t" + createBytecodeMessage((BytecodeHook) hook));
			else if(hook instanceof ClassHook)
				logger.info("\t" + createClassMessage((ClassHook) hook));
	}

	private String createGetterMessage(GetterHook getterHook) {
		String returnType = getterHook.getReturnType();
		if(returnType.startsWith("java.lang."))
			returnType = returnType.substring("java.lang.".length());
		else if(returnType.startsWith(hooksPackage))
			returnType = returnType.substring(hooksPackage.length());
		return "\t^ " + returnType + " " + getterHook.getGetterName()
				+ "() gets " + Type.getType(getterHook.getFieldSignature())
				+ " " + getterHook.getClassName() + "."
				+ getterHook.getFieldName();
	}

	private String createSetterMessage(SetterHook setterHook) {
		String argumentType = setterHook.getArgumentType();
		if(argumentType.startsWith("java.lang."))
			argumentType = argumentType.substring("java.lang.".length());
		else if(argumentType.startsWith(hooksPackage))
			argumentType = argumentType.substring(hooksPackage.length());
		return "\t^ void " + setterHook.getSetterName() + "(" + argumentType
				+ " " + setterHook.getFieldName() + ") sets "
				+ Type.getType(setterHook.getFieldSignature()) + " "
				+ setterHook.getClassName() + "." + setterHook.getFieldName();
	}

	private String createMethodMessage(MethodHook hook) {
		Type[] argumentTypes = Type.getArgumentTypes(hook.getMethodSignature());
		String argumentTypesTogether = "";
		for(int i = 0; i < argumentTypes.length; i++) {
			String argumentTypeName = argumentTypes[i].toString();
			if(argumentTypeName.startsWith("java.")) {
				String[] splitByPeriod = argumentTypeName.split("\\.");
				argumentTypeName = splitByPeriod[splitByPeriod.length - 1];
			}
			if(i == argumentTypes.length - 1) {
				argumentTypesTogether += argumentTypeName;
			} else {
				argumentTypesTogether += argumentTypeName + ", ";
			}
		}
		argumentTypes = Type.getArgumentTypes(hook.getNewMethodSignature());
		String newArgumentTypesTogether = "";
		for(int i = 0; i < argumentTypes.length; i++) {
			String argumentTypeName = argumentTypes[i].toString();
			if(argumentTypeName.startsWith("java.")) {
				String[] splitByPeriod = argumentTypeName.split("\\.");
				argumentTypeName = splitByPeriod[splitByPeriod.length - 1];
			} else if(argumentTypeName.startsWith(hooksPackage))
				argumentTypeName = argumentTypeName.substring(hooksPackage
						.length());
			if(i == argumentTypes.length - 1) {
				newArgumentTypesTogether += argumentTypeName;
			} else {
				newArgumentTypesTogether += argumentTypeName + ", ";
			}
		}
		String newMethodReturnTypeName = Type.getReturnType(
				hook.getNewMethodSignature()).toString();
		if(newMethodReturnTypeName.startsWith("java.")) {
			String[] splitByPeriod = newMethodReturnTypeName.split("\\.");
			newMethodReturnTypeName = splitByPeriod[splitByPeriod.length - 1];
		} else if(newMethodReturnTypeName.startsWith(hooksPackage))
			newMethodReturnTypeName = newMethodReturnTypeName
					.substring(hooksPackage.length());
		String methodReturnTypeName = Type.getReturnType(
				hook.getMethodSignature()).toString();
		if(methodReturnTypeName.startsWith("java.")) {
			String[] splitByPeriod = methodReturnTypeName.split("\\.");
			methodReturnTypeName = splitByPeriod[splitByPeriod.length - 1];
		} else if(methodReturnTypeName.startsWith(hooksPackage))
			methodReturnTypeName = methodReturnTypeName.substring(hooksPackage
					.length());
		String classGenName = hook.getClassName();
		String methodName = hook.getMethodName();
		return "\t^ " + newMethodReturnTypeName + " " + hook.getNewMethodName()
				+ "(" + newArgumentTypesTogether + ") calls "
				+ methodReturnTypeName + " " + classGenName + "." + methodName
				+ "(" + argumentTypesTogether + ")";
	}

	private String createCallbackMessage(CallbackHook hook) {
		String methodSignature = hook.getMethodName().substring(
				hook.getMethodName().split("\\(")[0].length());
		Type[] argumentTypes = Type.getArgumentTypes(methodSignature);
		String argumentTypesTogether = "";
		for(int i = 0; i < argumentTypes.length; i++) {
			String argumentTypeName = argumentTypes[i].toString();
			if(argumentTypeName.startsWith("java.")) {
				String[] splitByPeriod = argumentTypeName.split("\\.");
				argumentTypeName = splitByPeriod[splitByPeriod.length - 1];
			}
			if(i == argumentTypes.length - 1) {
				argumentTypesTogether += argumentTypeName;
			} else {
				argumentTypesTogether += argumentTypeName + ", ";
			}
		}
		String methodReturnTypeName = Type.getReturnType(methodSignature)
				.toString();
		if(methodReturnTypeName.startsWith("java.")) {
			String[] splitByPeriod = methodReturnTypeName.split("\\.");
			methodReturnTypeName = splitByPeriod[splitByPeriod.length - 1];
		}
		String classGenName = hook.getClassName();
		String methodName = hook.getMethodName();
		return "» Callback to " + hook.getInterfaceName() + "."
				+ hook.getCallbackMethod() + "() inserted into " + classGenName
				+ "." + methodName + "(" + argumentTypesTogether
				+ ") at position " + hook.getPosition();
	}

	private String createBytecodeMessage(BytecodeHook hook) {
		Type[] argumentTypes = Type.getArgumentTypes(hook.getMethodSignature());
		String argumentTypesTogether = "";
		for(int i = 0; i < argumentTypes.length; i++) {
			String argumentTypeName = argumentTypes[i].toString();
			if(argumentTypeName.startsWith("java.")) {
				String[] splitByPeriod = argumentTypeName.split("\\.");
				argumentTypeName = splitByPeriod[splitByPeriod.length - 1];
			}
			if(i == argumentTypes.length - 1) {
				argumentTypesTogether += argumentTypeName;
			} else {
				argumentTypesTogether += argumentTypeName + ", ";
			}
		}
		String methodReturnTypeName = Type.getReturnType(
				hook.getMethodSignature()).toString();
		if(methodReturnTypeName.startsWith("java.")) {
			String[] splitByPeriod = methodReturnTypeName.split("\\.");
			methodReturnTypeName = splitByPeriod[splitByPeriod.length - 1];
		}
		String classGenName = hook.getClassName();
		String methodName = hook.getMethodName();
		int instructionCount = 0;
		int newConstantCount = 0;
		XMLInstruction[] instructions = hook.getInstructions();
		for(XMLInstruction instruction : instructions)
			if(instruction instanceof BCELInstruction)
				instructionCount++;
			else if(instruction instanceof AddConstInstruction)
				newConstantCount++;
		return "# Bytecode added to " + methodReturnTypeName + " "
				+ classGenName + "." + methodName + "(" + argumentTypesTogether
				+ ") at position " + hook.getPosition() + " consisting of "
				+ (instructionCount == 0 ? "no" : instructionCount)
				+ " new instruction" + (instructionCount != 1 ? "s" : "")
				+ " and " + (newConstantCount == 0 ? "no" : newConstantCount)
				+ " new constant" + (newConstantCount != 1 ? "s" : "");
	}

	private String createClassMessage(ClassHook hook) {
		String superclassName = hook.getSuperclassName();
		superclassName = superclassName.substring(superclassName
				.lastIndexOf(".") + 1);
		return "+ " + superclassName + " is extended by " + hook.getClassName();
	}

	private boolean containsType(List<Transformer> transformers,
			Class<? extends Transformer> type) {
		for(Transformer transformer : transformers)
			if(type.isInstance(transformer))
				return true;
		return false;
	}

	public void printFailed() {
		int fails = 0;
		for(Transformer transformer : transformers) {
			fails += transformer.getFailedHooks().length;
		}
		if(fails == 0)
			return;
		logger.info("Fails");
		for(Transformer transformer : transformers) {
			Hook[] failedHooks = transformer.getFailedHooks();
			if(failedHooks.length > 0) {
				logger.info("\t" + transformer.getClass().getName());
				for(Hook hook : failedHooks) {
					if(hook instanceof InterfaceHook) {
						logger.info("\t\t* Making a class implement "
								+ ((InterfaceHook) hook).getInterfaceName());
					} else if(hook instanceof GetterHook) {
						GetterHook getterHook = (GetterHook) hook;
						logger.info("\t\t^ Adding getter "
								+ getterHook.getGetterName() + "() to "
								+ getterHook.getInterfaceName());
					} else if(hook instanceof MethodHook) {
						MethodHook methodHook = (MethodHook) hook;
						logger.info("\t\t^ Adding method "
								+ methodHook.getMethodName() + " to "
								+ methodHook.getInterfaceName());
					}
				}
			}
		}
		logger.info("");
	}

	/*	public byte[] generateData() {
			logger.info("Outputting jar...");
			try {
				ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
				JarOutputStream out = new JarOutputStream(byteArrayOut);
				for(ClassGen classGen : classes) {
					JarEntry jarEntry = new JarEntry(classGen.getClassName()
							.replace('.', '/') + ".class");
					out.putNextEntry(jarEntry);
					out.write(classGen.getJavaClass().getBytes());
				}
				for(String entry : entries.keySet()) {
					JarEntry jarEntry = new JarEntry(entry);
					out.putNextEntry(jarEntry);
					out.write(entries.get(entry));
				}
				out.close();
				byteArrayOut.close();
				return byteArrayOut.toByteArray();
			} catch(Exception e) {
				e.printStackTrace();
				logger.throwing(getClass().getName(), "generateData", e);
				return null;
			}
		}*/

	public String generateXML() {
		logger.info("Dumping hooks...");
		Element root = new Element("hooks");
		root.setAttribute("type", "client");
		if(dataVersion != null)
			root.setAttribute("version", dataVersion);
		HashMap<String, InterfaceHook> interfaceMappings = new HashMap<String, InterfaceHook>();
		HashMap<String, Vector<Hook>> interfaceHookMappings = new HashMap<String, Vector<Hook>>();
		for(Transformer transformer : transformers) {
			for(Hook hook : transformer.getHooks()) {
				if(!(hook instanceof InterfaceHook))
					continue;
				InterfaceHook interfaceHook = (InterfaceHook) hook;
				String interfaceName = interfaceHook.getInterfaceName();
				interfaceMappings.put(interfaceName, interfaceHook);
				interfaceHookMappings.put(interfaceName, new Vector<Hook>());
			}
		}
		for(Transformer transformer : transformers) {
			for(Hook hook : transformer.getHooks()) {
				if(hook instanceof CallbackHook || hook instanceof BytecodeHook
						|| hook instanceof ClassHook) {
					root.addContent(hook.toXML());
					continue;
				} else if(hook instanceof InterfaceHook)
					continue;
				interfaceHookMappings.get(hook.getInterfaceName()).add(hook);
			}
		}
		for(String interfaceName : interfaceHookMappings.keySet()) {
			InterfaceHook interfaceHook = interfaceMappings.get(interfaceName);
			Element interfaceElement = interfaceHook.toXML();
			for(Hook hook : interfaceHookMappings.get(interfaceName))
				interfaceElement.addContent(hook.toXML());
			root.addContent(interfaceElement);
		}
		Document document = new Document(root);
		try {
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			StringWriter writer = new StringWriter();
			outputter.output(document, writer);
			return writer.toString();
		} catch(FileNotFoundException exception) {
			exception.printStackTrace();
		} catch(IOException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public ClassVector getClasses() {
		return classes;
	}

	public Transformer[] getTransformers() {
		return transformers.toArray(new Transformer[transformers.size()]);
	}

	public String getHooksPackage() {
		return hooksPackage;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(String dataVersion) {
		this.dataVersion = dataVersion;
	}
}
