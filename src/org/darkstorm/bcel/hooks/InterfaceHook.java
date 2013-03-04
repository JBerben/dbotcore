package org.darkstorm.bcel.hooks;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ClassGen;
import org.darkstorm.bcel.Injector;
import org.jdom.Element;

public class InterfaceHook extends Hook {
	private String className, interfaceName;
	private String[] interfaces;

	public InterfaceHook(Element element) {
		super(element);
	}

	public InterfaceHook(String className, String interfaceName,
			String... interfaces) {
		this.className = className;
		this.interfaceName = interfaceName;
		this.interfaces = interfaces;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getInterfaceName() {
		return interfaceName;
	}

	public String[] getInterfaces() {
		return interfaces;
	}

	@Override
	public Element toXML() {
		Element element = new Element("interface");
		element.setAttribute("class", className);
		element.setAttribute("interface", interfaceName);
		if(interfaces.length > 0) {
			String interfaceNames = interfaces[0];
			for(int i = 1; i < interfaces.length; i++)
				interfaceNames += "," + interfaces[i];
			element.setAttribute("interfaces", interfaceNames);
		}
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		interfaceName = element.getAttributeValue("interface");
		String interfaceNames = element.getAttributeValue("interfaces");
		if(interfaceNames != null)
			interfaces = interfaceNames.split(",");
		else
			interfaces = new String[0];
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		classGen.addInterface(interfaceName);
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		classGen = new ClassGen(interfaceName, "java.lang.Object",
				"Class.java", Constants.ACC_PUBLIC | Constants.ACC_INTERFACE
						| Constants.ACC_ABSTRACT, interfaces);
		return classGen;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof InterfaceHook))
			return false;
		outer: for(String interface1 : interfaces) {
			for(String interface2 : ((InterfaceHook) hook).getInterfaces())
				if(interface1.equals(interface2))
					continue outer;
			return false;
		}
		return interfaceName.equals(hook.getInterfaceName());
	}
}
