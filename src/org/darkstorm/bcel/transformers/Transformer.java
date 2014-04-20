package org.darkstorm.bcel.transformers;

import java.util.*;
import java.util.logging.Logger;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.hooks.*;
import org.darkstorm.bcel.util.*;

/**
 * @author DarkStorm
 */
@SuppressWarnings("deprecation")
public abstract class Transformer {
	protected static HashMap<String, String> interfaceMap = new HashMap<String, String>();

	protected final Updater updater;
	protected final List<Hook> hooks;
	protected final List<Hook> expectedHooks;
	protected final List<Hook> failedHooks;
	protected final Logger logger;

	protected boolean hooked;

	public Transformer(Updater updater) {
		hooks = new Vector<Hook>();
		expectedHooks = new Vector<Hook>();
		failedHooks = new Vector<Hook>();
		this.updater = updater;
		logger = updater.getLogger();
	}

	public final void attemptToUpdate(ClassGen classGen) {
		if(isLocatedIn(classGen)) {
			updateHook(classGen);
			hooked = true;
		}
	}

	public abstract boolean isLocatedIn(ClassGen classGen);

	public abstract void updateHook(ClassGen classGen);

	public void checkForFails() {
		outer: for(Hook expected : expectedHooks) {
			for(Hook hook : hooks)
				if(expected.hasSameTargetAs(hook))
					continue outer;
			failedHooks.add(expected);
		}
	}

	public boolean hasBeenHooked() {
		return hooked;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends Transformer>[] getRequiredTransformers() {
		return new Class[0];
	}

	public Hook[] getFailedHooks() {
		return failedHooks.toArray(new Hook[0]);
	}

	public Hook[] getHooks() {
		return hooks.toArray(new Hook[0]);
	}

	protected Transformer getTransformerByType(Class<? extends Transformer> type) {
		for(Transformer transformer : updater.getTransformers()) {
			if(type.isInstance(transformer))
				return transformer;
		}
		return null;
	}

	protected Hook getHookByClassAndPartName(Transformer transformer,
			Class<? extends Hook> hookClass, String hookObjectName) {
		for(Hook hook : transformer.getHooks()) {
			if(hookClass.isInstance(hook)) {
				String hookNameToMatch;
				if(hook instanceof GetterHook)
					hookNameToMatch = ((GetterHook) hook).getGetterName();
				else if(hook instanceof InterfaceHook)
					hookNameToMatch = ((InterfaceHook) hook).getInterfaceName();
				else if(hook instanceof MethodHook)
					hookNameToMatch = ((MethodHook) hook).getMethodName();
				else
					throw new IllegalArgumentException(
							"type must be a "
									+ "GetterHook, SetterHook, InterfaceHook, or MethodHook");
				if(hookObjectName.equals(hookNameToMatch))
					return hook;
			}
		}
		return null;
	}

	protected void addExpectedInterface(String interfaceName) {
		expectedHooks.add(new InterfaceHook("", updater.getHooksPackage()
				+ interfaceName));
	}

	protected void addExpectedGetter(String interfaceName, String getterName,
			String getterReturnType) {
		expectedHooks.add(new GetterHook("", updater.getHooksPackage()
				+ interfaceName, "", "", false, getterReturnType, getterName));
	}

	protected void addExpectedSetter(String interfaceName, String setterName,
			String setterFieldType) {
		expectedHooks.add(new SetterHook("", updater.getHooksPackage()
				+ interfaceName, "", "", false, setterFieldType, setterName));
	}

	protected void addFailedInterface(String interfaceName) {
		failedHooks.add(new InterfaceHook("", updater.getHooksPackage()
				+ interfaceName));
	}

	protected void addFailedGetter(String interfaceName, String getterName,
			String getterReturnType) {
		failedHooks.add(new GetterHook("", updater.getHooksPackage()
				+ interfaceName, "", "", false, getterReturnType, getterName));
	}

	protected void addFailedSetter(String interfaceName, String setterName,
			String setterFieldType) {
		failedHooks.add(new SetterHook("", updater.getHooksPackage()
				+ interfaceName, "", "", false, setterFieldType, setterName));
	}

	protected void addClass(ClassGen cg, String newSuperclass) {
		hooks.add(new ClassHook(cg.getClassName(), newSuperclass));
	}

	protected void addInterface(ClassGen cg, String interfaceName,
			String... interfaces) {
		interfaces = Arrays.copyOf(interfaces, interfaces.length);
		for(int i = 0; i < interfaces.length; i++)
			if(!interfaces[i].contains("."))
				interfaces[i] = updater.getHooksPackage() + interfaces[i];
		hooks.add(new InterfaceHook(cg.getClassName(), updater
				.getHooksPackage() + interfaceName, interfaces));
	}

	protected void addGetter(FieldInstruction fi, String interfaceName,
			ConstantPoolGen cpg, String name) {
		ClassGen classGen = updater.getClasses()
				.getByName(fi.getClassName(cpg));
		Field field = getFieldByName(classGen, fi.getFieldName(cpg));
		addGetter(classGen, interfaceName, field, name);
	}

	protected void addGetterSetter(FieldInstruction fi, String interfaceName,
			ConstantPoolGen cpg, String getterName, String setterName) {
		ClassGen classGen = updater.getClasses()
				.getByName(fi.getClassName(cpg));
		Field field = getFieldByName(classGen, fi.getFieldName(cpg));
		addGetterSetter(classGen, interfaceName, field, getterName, setterName);
	}

	protected void addGetterSetter(ClassGen classGen, String interfaceName,
			Field field, String getterName, String setterName) {
		addGetter(classGen, interfaceName, field, getterName);
		addSetter(classGen, interfaceName, field, setterName);
	}

	protected void addGetter(ClassGen classGen, String interfaceName,
			Field field, String name) {
		String className = classGen.getClassName();
		if(field.isStatic()) {
			interfaceName = "Client";
			className = "client";
		}
		Type fieldType = field.getType();
		String returnType = getInterfaceClassNameIfExists(fieldType.toString());
		hooks.add(new GetterHook(className, updater.getHooksPackage()
				+ interfaceName, classGen.getClassName(), field.getName(),
				field.getSignature(), field.isStatic(), returnType, name));
	}

	protected void addSetter(FieldInstruction fi, String interfaceName,
			ConstantPoolGen cpg, String name) {
		ClassGen classGen = updater.getClasses()
				.getByName(fi.getClassName(cpg));
		Field field = getFieldByName(classGen, fi.getFieldName(cpg));
		addSetter(classGen, interfaceName, field, name);
	}

	protected void addSetter(ClassGen classGen, String interfaceName,
			Field field, String name) {
		if(field.isStatic())
			interfaceName = "client.Minecraft";
		String returnType = getInterfaceClassNameIfExists(Type.getType(
				field.getSignature()).toString());
		hooks.add(new SetterHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, field.getName(), field
				.getSignature(), field.isStatic(), returnType, name));
	}

	protected void addMethod(ClassGen classGen, String interfaceName,
			Method method, String newMethodName) {
		if(method.isStatic())
			interfaceName = "client.Minecraft";
		String newMethodSignature = "(";
		for(Type argumentType : method.getArgumentTypes()) {
			String argumentClass = argumentType.toString();
			String properClass = getInterfaceClassNameIfExists(argumentClass);
			Type properType = Util.getType(properClass);
			newMethodSignature += properType.getSignature();
		}
		Type returnType = method.getReturnType();
		String returnClass = returnType.toString();
		String properReturnClass = getInterfaceClassNameIfExists(returnClass);
		Type properReturnType = Util.getType(properReturnClass);
		newMethodSignature += ")" + properReturnType.getSignature();
		hooks.add(new MethodHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, method.getName(), method
				.getSignature(), newMethodName, newMethodSignature));
	}

	private String getInterfaceClassNameIfExists(String className) {
		String properClassName = className;
		transformLabel: for(Transformer transformer : updater.getTransformers()) {
			for(Hook hook : transformer.getHooks()) {
				if(!(hook instanceof InterfaceHook))
					continue;

				InterfaceHook interfaceHook = (InterfaceHook) hook;
				String interfaceClassName = interfaceHook.getClassName();
				String argumentBaseClass = className.replaceAll("[\\[\\]]", "");

				if(interfaceClassName.equals(argumentBaseClass)) {
					String brackets = "";
					if(className.contains("["))
						brackets = className.substring(className.indexOf("["));
					properClassName = interfaceHook.getInterfaceName()
							+ brackets;
					break transformLabel;
				}
			}
		}
		return properClassName;
	}

	protected void addCallback(ClassGen classGen, String interfaceName,
			Method method, int position, String callbackMethod,
			String... values) {
		hooks.add(new CallbackHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, method.getName()
				+ method.getSignature(), position, callbackMethod, values));
	}

	protected void addCallback(ClassGen classGen, String interfaceName,
			Method method, int position, String callbackMethod,
			int cancelTarget, String... values) {
		hooks.add(new CallbackHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, method.getName()
				+ method.getSignature(), position, callbackMethod,
				cancelTarget, values));
	}

	protected void addCallback(ClassGen classGen, String interfaceName,
			Method method, int position, String callbackMethod,
			int cancelTarget, int callEventTarget, String... values) {
		hooks.add(new CallbackHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, method.getName()
				+ method.getSignature(), position, callbackMethod,
				cancelTarget, callEventTarget, values));
	}

	protected void addCallback(ClassGen classGen, String interfaceName,
			Method method, int position, String callbackMethod,
			int cancelTarget, byte storeVar, String... values) {
		hooks.add(new CallbackHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, method.getName()
				+ method.getSignature(), position, callbackMethod,
				cancelTarget, storeVar, values));
	}

	protected void addCallback(ClassGen classGen, String interfaceName,
			Method method, int position, String callbackMethod, byte storeVar,
			String... values) {
		hooks.add(new CallbackHook(classGen.getClassName(), updater
				.getHooksPackage() + interfaceName, method.getName()
				+ method.getSignature(), position, callbackMethod, storeVar,
				values));
	}

	protected String toCallbackConstant(Number n) {
		try {
			java.lang.reflect.Field field = n.getClass().getField("TYPE");
			String name = ((Class<?>) field.get(null)).getSimpleName();
			return name + ":" + n.toString();
		} catch(Exception exception) {
			throw new RuntimeException();
		}
	}

	protected String toCallbackConstant(Character c) {
		return "char:" + ((int) c.charValue());
	}

	protected String toCallbackConstant(Boolean b) {
		return "bool:" + b.toString();
	}

	protected String toCallbackConstant(String s) {
		return "string:" + s;
	}

	protected String toCallbackLocalVar(Type type, int index) {
		return "var:" + type.getSignature() + ":" + index;
	}

	public static String[] split(String str, String regex) {
		String replacer = new String(str.toCharArray());
		Vector<String> strings = new Vector<String>();
		while(replacer.contains(regex)) {
			int i = replacer.indexOf(regex);
			strings.add(replacer.substring(0, i));
			replacer = replacer.substring(i + regex.length());
		}
		strings.add(replacer);
		return strings.toArray(new String[] {});
	}

	protected Method getMethodWith(ClassGen classGen,
			Class<? extends Instruction> type, String value) {
		for(Method method : classGen.getMethods()) {
			InstructionSearcher is = new InstructionSearcher(classGen, method);
			while(is.getNext(type) != null) {
				if(is.getCurrent() instanceof LDC) {
					LDC ldci = (LDC) is.getCurrent();
					if(ldci.getValue(is.getConstantPoolGen()).equals(value))
						return method;
				}
				if(is.getCurrent() instanceof NEW) {
					NEW newi = (NEW) is.getCurrent();
					if(newi.getType(classGen.getConstantPool()).toString()
							.equals(value))
						return method;
				}
			}
		}
		return null;
	}

	protected Method getMethodWithConstant(ClassGen classGen, Object constant) {
		for(Method method : classGen.getMethods()) {
			InstructionSearcher is = new InstructionSearcher(classGen, method);
			if(is.getNextLDC(constant) != null)
				return method;
		}
		return null;
	}

	protected Method[] getMethodsWithConstant(ClassGen classGen, Object constant) {
		Vector<Method> methods = new Vector<Method>();
		for(Method method : classGen.getMethods()) {
			InstructionSearcher is = new InstructionSearcher(classGen, method);
			if(is.getNextLDC(constant) != null)
				methods.add(method);
		}
		return methods.toArray(new Method[methods.size()]);
	}

	protected Method getMethod(ClassGen classGen, String methodName, String sig) {
		for(Method method : classGen.getMethods())
			if(method.getName().equals(methodName)
					&& method.getSignature().equals(sig))
				return method;
		return null;
	}

	protected Method getMethod(ClassGen classGen, String methodName) {
		for(Method method : classGen.getMethods())
			if(method.getName().equals(methodName))
				return method;
		return null;
	}

	protected Method getMethod(ClassGen classGen, InvokeInstruction invoke) {
		ConstantPoolGen constantPool = classGen.getConstantPool();
		ClassVector classes = updater.getClasses();
		if(!invoke.getClassName(constantPool).equals(classGen.getClassName()))
			classGen = classes.getByName(invoke.getClassName(constantPool));
		Method method = null;
		while(classGen != null
				&& (method = getMethod(classGen,
						invoke.getMethodName(constantPool),
						invoke.getSignature(constantPool))) == null)
			classGen = classes.getByName(classGen.getSuperclassName());
		return method;
	}

	protected Field getFieldByName(ClassGen classGen, String fieldName) {
		for(Field field : classGen.getFields())
			if(field.getName().equals(fieldName))
				return field;
		return null;
	}

	protected Field getFieldBySignature(ClassGen classGen, String signature) {
		for(Field field : classGen.getFields())
			if(field.getSignature().equals(signature))
				return field;
		return null;
	}

}