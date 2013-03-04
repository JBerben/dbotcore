package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.oldschool.OldSchoolBot;
import org.jdom.Element;

public class CallbackHook extends Hook {
	private String className;
	private String interfaceName;
	private String method;
	private int position;
	private String callbackMethod;
	private String[] values;
	private int cancelTarget = -1;
	private int callEventTarget = -1;
	private int storeVar = -1;

	public CallbackHook(Element element) {
		super(element);
	}

	public CallbackHook(String className, String interfaceName, String method,
			int position, String callbackMethod, String... values) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.method = method;
		this.position = position;
		this.callbackMethod = callbackMethod;
		this.values = values;
	}

	public CallbackHook(String className, String interfaceName, String method,
			int position, String callbackMethod, int cancelTarget,
			String... values) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.method = method;
		this.position = position;
		this.callbackMethod = callbackMethod;
		this.cancelTarget = cancelTarget;
		this.values = values;
	}

	public CallbackHook(String className, String interfaceName, String method,
			int position, String callbackMethod, int cancelTarget,
			int callEventTarget, String... values) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.method = method;
		this.position = position;
		this.callbackMethod = callbackMethod;
		this.cancelTarget = cancelTarget;
		this.callEventTarget = callEventTarget;
		this.values = values;
	}

	public CallbackHook(String className, String interfaceName, String method,
			int position, String callbackMethod, int cancelTarget,
			byte storeVar, String... values) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.method = method;
		this.position = position;
		this.callbackMethod = callbackMethod;
		this.cancelTarget = cancelTarget;
		this.storeVar = storeVar;
		this.values = values;
	}

	public CallbackHook(String className, String interfaceName, String method,
			int position, String callbackMethod, byte storeVar,
			String... values) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.method = method;
		this.position = position;
		this.callbackMethod = callbackMethod;
		this.storeVar = storeVar;
		this.values = values;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getInterfaceName() {
		return interfaceName;
	}

	public String getMethodName() {
		return method;
	}

	public int getPosition() {
		return position;
	}

	public String getCallbackMethod() {
		return callbackMethod;
	}

	public int getCancelTarget() {
		return cancelTarget;
	}

	public int getCallEventTarget() {
		return callEventTarget;
	}

	public int getStoreVar() {
		return storeVar;
	}

	public String[] getValues() {
		return values.clone();
	}

	@Override
	public Element toXML() {
		Element element = new Element("callback");
		element.setAttribute("class", className);
		element.setAttribute("interface", interfaceName);
		element.setAttribute("method", method);
		element.setAttribute("position", Integer.toString(position));
		element.setAttribute("callback", callbackMethod);
		if(values.length > 0) {
			String attribute = values[0];
			for(int i = 1; i < values.length; i++)
				attribute = attribute + "," + values[i];
			element.setAttribute("values", attribute);
		}
		if(cancelTarget != -1)
			element.setAttribute("cancel", Integer.toString(cancelTarget));
		if(callEventTarget != -1)
			element.setAttribute("call", Integer.toString(callEventTarget));
		if(storeVar != -1)
			element.setAttribute("store", Integer.toString(storeVar));
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		interfaceName = element.getAttributeValue("interface");
		method = element.getAttributeValue("method");
		position = Integer.parseInt(element.getAttributeValue("position"));
		callbackMethod = element.getAttributeValue("callback");
		String valuesAttribute = element.getAttributeValue("values");
		if(valuesAttribute != null) {
			String[] values = valuesAttribute.split(",");
			if(values.length != 1 || !values[0].isEmpty())
				this.values = values;
		} else
			values = new String[0];
		String cancelAttribute = element.getAttributeValue("cancel");
		if(cancelAttribute != null)
			cancelTarget = Integer.parseInt(cancelAttribute);
		else
			cancelTarget = -1;
		String callAttribute = element.getAttributeValue("call");
		if(callAttribute != null)
			callEventTarget = Integer.parseInt(callAttribute);
		else
			callEventTarget = -1;
		String storeAttribute = element.getAttributeValue("store");
		if(storeAttribute != null)
			storeVar = Integer.parseInt(storeAttribute);
		else
			storeVar = -1;

	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		String methodName = method.split("\\(")[0];
		String methodSignature = method.substring(methodName.length());
		Method originalMethod = null;
		MethodGen methodForCallback = null;
		for(Method method : classGen.getMethods()) {
			if(methodName.equals(method.getName())
					&& methodSignature.equals(method.getSignature())) {
				originalMethod = method;
				methodForCallback = new MethodGen(method, className,
						classGen.getConstantPool());
			}
		}
		InstructionList instructionList = methodForCallback
				.getInstructionList();
		InstructionFactory factory = new InstructionFactory(classGen);
		String callbackMethodName = callbackMethod.split("\\(")[0];
		String callbackMethodSignature = callbackMethod
				.substring(callbackMethodName.length());
		int offset = getOffsetAtPosition(methodForCallback, position);
		InstructionHandle handleForInsertion = instructionList
				.findHandle(position + offset);
		InstructionList newList = new InstructionList();
		for(String value : values) {
			if(value.startsWith("string:"))
				newList.append(factory.createConstant(value.substring("string:"
						.length())));
			else if(value.startsWith("int:"))
				newList.append(factory.createConstant(Integer.valueOf(value
						.substring("int:".length()))));
			else if(value.startsWith("long:"))
				newList.append(factory.createConstant(Long.valueOf(value
						.substring("long:".length()))));
			else if(value.startsWith("short:"))
				newList.append(factory.createConstant(Short.valueOf(value
						.substring("short:".length()))));
			else if(value.startsWith("double:"))
				newList.append(factory.createConstant(Double.valueOf(value
						.substring("double:".length()))));
			else if(value.startsWith("float:"))
				newList.append(factory.createConstant(Float.valueOf(value
						.substring("float:".length()))));
			else if(value.startsWith("byte:"))
				newList.append(factory.createConstant(Byte.valueOf(value
						.substring("byte:".length()))));
			else if(value.startsWith("char:"))
				newList.append(factory.createConstant((char) Integer
						.parseInt(value.substring("char:".length()))));
			else if(value.startsWith("bool:"))
				newList.append(factory.createConstant(Boolean.valueOf(value
						.substring("bool:".length()))));
			else if(value.startsWith("var:")) {
				String[] parts = value.split(":");
				String signature = parts[parts.length - 2];
				int index = Integer.parseInt(parts[parts.length - 1]);
				newList.append(InstructionFactory.createLoad(
						Type.getType(signature), index));
			}
		}
		Type returnType = Type.getReturnType(callbackMethodSignature);
		ObjectType callbackEventType = (ObjectType) Type
				.getType(CallbackEvent.class);
		Instruction invoke = factory.createInvoke(interfaceName,
				callbackMethodName, callbackEventType,
				Type.getArgumentTypes(callbackMethodSignature),
				Constants.INVOKESTATIC);
		newList.append(invoke);
		if(cancelTarget != -1) {
			int localVarCount = methodForCallback.isStatic() ? 0 : 1;
			localVarCount += methodForCallback.getArgumentTypes().length;
			for(Instruction i : instructionList.getInstructions())
				if(i instanceof StoreInstruction)
					localVarCount++;
			newList.append(new ASTORE(localVarCount));
			newList.append(new ALOAD(localVarCount));
			newList.append(factory.createInvoke(
					callbackEventType.getClassName(), "isCancelled",
					Type.BOOLEAN, new Type[0], Constants.INVOKEVIRTUAL));
			newList.append(new IFNE(instructionList.findHandle(cancelTarget
					+ getOffsetAtPosition(methodForCallback, cancelTarget))));
			if(callEventTarget != -1) {
				instructionList.insert(
						instructionList.findHandle(callEventTarget
								+ getOffsetAtPosition(methodForCallback,
										callEventTarget)), newList);
				updateMethodPositions(methodForCallback, callEventTarget,
						newList.getByteCode().length);
				newList = new InstructionList();
			}
			newList.append(new ALOAD(localVarCount));
		}

		if(!returnType.equals(Type.VOID)) {
			newList.append(factory.createInvoke(
					callbackEventType.getClassName(), "getReturnObject",
					Type.OBJECT, new Type[0], Constants.INVOKEVIRTUAL));
			if(!returnType.equals(Type.OBJECT))
				if(returnType instanceof BasicType) {
					Class<?> autoboxType = getAutoboxType((BasicType) returnType);
					newList.append(factory.createCheckCast((ObjectType) Type
							.getType(autoboxType)));
					newList.append(factory.createInvoke(autoboxType.getName(),
							Constants.TYPE_NAMES[returnType.getType()]
									+ "Value", returnType, new Type[0],
							Constants.INVOKEVIRTUAL));
				} else
					newList.append(factory
							.createCheckCast((ReferenceType) returnType));
			if(storeVar != -1)
				newList.append(InstructionFactory.createStore(returnType,
						storeVar));
		} else
			newList.append(new POP());

		updateMethodPositions(methodForCallback, position,
				newList.getByteCode().length);

		instructionList.insert(handleForInsertion, newList);
		instructionList.setPositions();
		methodForCallback.setInstructionList(instructionList);
		methodForCallback.setMaxLocals();
		methodForCallback.setMaxStack();
		classGen.replaceMethod(originalMethod, methodForCallback.getMethod());
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		if(classGen == null) {
			classGen = new ClassGen(interfaceName, "java.lang.Object",
					"Class.java", Constants.ACC_PUBLIC | Constants.ACC_FINAL,
					new String[0]);
		}
		String methodName = callbackMethod.split("\\(")[0];
		String methodSignature = callbackMethod.substring(methodName.length());
		// String darkModClassName = "org.darkstorm.minecraft.darkmod.DarkMod";
		// String modHandlerClassName =
		// "org.darkstorm.minecraft.darkmod.mod.ModHandler";
		// String eventManagerClassName =
		// "org.darkstorm.tools.events.EventManager";
		// String eventClassName = "org.darkstorm.tools.events.Event";
		Type returnType = Type.getType(CallbackEvent.class);
		Type[] argTypes = Type.getArgumentTypes(methodSignature);

		for(Method method : classGen.getMethods())
			if(methodName.equals(method.getName())
					&& returnType.getSignature().equals(
							method.getSignature().split("\\)")[1]))
				return classGen;

		InstructionList list = new InstructionList();

		MethodGen method = new MethodGen(Constants.ACC_PUBLIC
				| Constants.ACC_STATIC, returnType, argTypes, null, methodName,
				classGen.getClassName(), list, classGen.getConstantPool());
		InstructionFactory factory = new InstructionFactory(classGen);

		list.append(factory.createFieldAccess("client", "bot",
				Type.getType(OldSchoolBot.class), Constants.GETSTATIC));
		list.append(factory.createInvoke(OldSchoolBot.class.getName(),
				"getEventManager", Type.getType(EventManager.class),
				new Type[0], Constants.INVOKEVIRTUAL));

		/*list.append(factory.createInvoke(darkModClassName, "getInstance",
				new ObjectType(darkModClassName), new Type[0],
				Constants.INVOKESTATIC));
		list.append(factory.createInvoke(darkModClassName, "getModHandler",
				new ObjectType(modHandlerClassName), new Type[0],
				Constants.INVOKEVIRTUAL));
		list.append(factory.createInvoke(modHandlerClassName,
				"getEventManager", new ObjectType(eventManagerClassName),
				new Type[0], Constants.INVOKEVIRTUAL));*/
		list.append(new ASTORE(argTypes.length));

		list.append(factory.createNew(new ObjectType(CallbackEvent.class
				.getName())));
		list.append(new DUP());

		list.append(factory.createConstant(methodName));

		list.append(factory.createConstant(Integer.valueOf(argTypes.length)));
		list.append(factory.createNewArray(Type.OBJECT, (short) 1));
		for(int i = 0; i < argTypes.length; i++) {
			list.append(new DUP());
			list.append(factory.createConstant(Integer.valueOf(i)));
			list.append(InstructionFactory.createLoad(argTypes[i], i));
			if(argTypes[i] instanceof BasicType) {
				ObjectType autoboxType = (ObjectType) Type
						.getType(getAutoboxType((BasicType) argTypes[i]));
				list.append(factory.createInvoke(autoboxType.getClassName(),
						"valueOf", autoboxType, new Type[] { argTypes[i] },
						Constants.INVOKESTATIC));
			}
			list.append(new AASTORE());
		}

		list.append(factory.createInvoke(CallbackEvent.class.getName(),
				"<init>", Type.VOID, new Type[] { Type.STRING,
						new ArrayType(Type.OBJECT, 1) },
				Constants.INVOKESPECIAL));
		list.append(new ASTORE(argTypes.length + 1));

		list.append(new ALOAD(argTypes.length));
		list.append(new ALOAD(argTypes.length + 1));
		list.append(factory.createInvoke(EventManager.class.getName(),
				"sendEvent", Type.VOID,
				new Type[] { new ObjectType(Event.class.getName()) },
				Constants.INVOKEVIRTUAL));

		list.append(new ALOAD(argTypes.length + 1));
		list.append(InstructionFactory.createReturn(returnType));
		list.setPositions();
		method.setInstructionList(list);
		method.setMaxLocals();
		method.setMaxStack();
		classGen.addMethod(method.getMethod());
		return classGen;
	}

	private Class<?> getAutoboxType(BasicType type) {
		Class<?> autoboxType = null;
		switch(type.getType()) {
		case Constants.T_INT:
			autoboxType = Integer.class;
			break;
		case Constants.T_SHORT:
			autoboxType = Short.class;
			break;
		case Constants.T_LONG:
			autoboxType = Long.class;
			break;
		case Constants.T_BYTE:
			autoboxType = Byte.class;
			break;
		case Constants.T_DOUBLE:
			autoboxType = Double.class;
			break;
		case Constants.T_FLOAT:
			autoboxType = Float.class;
			break;
		case Constants.T_CHAR:
			autoboxType = Character.class;
			break;
		case Constants.T_BOOLEAN:
			autoboxType = Boolean.class;
			break;
		}
		return autoboxType;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof CallbackHook))
			return false;
		CallbackHook callback = (CallbackHook) hook;
		return interfaceName.equals(callback.getInterfaceName())
				&& callbackMethod.equals(callback.getCallbackMethod());
	}
}
