package org.darkstorm.bcel.hooks;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.darkstorm.bcel.Injector;
import org.jdom.Element;

public class ClassHook extends Hook {
	private String className, superclassName;

	public ClassHook(Element element) {
		super(element);
	}

	public ClassHook(String className, String superclassName) {
		this.className = className;
		this.superclassName = superclassName;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getInterfaceName() {
		return "extension";
	}

	public String getSuperclassName() {
		return superclassName;
	}

	@Override
	public Element toXML() {
		Element element = new Element("class");
		element.setAttribute("class", className);
		element.setAttribute("superclass", superclassName);
		return element;
	}

	@Override
	protected void fromXML(Element element) {
		className = element.getAttributeValue("class");
		superclassName = element.getAttributeValue("superclass");
	}

	@Override
	public boolean isInjectable(Injector injector, ClassGen classGen) {
		return className.equals(classGen.getClassName());
	}

	@Override
	public void inject(Injector injector, ClassGen classGen) {
		String previousSuperclass = classGen.getSuperclassName();
		classGen.setSuperclassName(superclassName);
		ConstantPoolGen constantPool = classGen.getConstantPool();
		int index = constantPool.lookupMethodref(previousSuperclass, "<init>",
				"()V");
		if(index != -1) {
			Constant newConstant = new ConstantMethodref(
					constantPool.addClass(superclassName),
					constantPool.addNameAndType("<init>", "()V"));
			constantPool.setConstant(index, newConstant);
		}
	}

	@Override
	public ClassGen generateInterface(ClassGen classGen) {
		return classGen;
	}

	@Override
	public boolean hasSameTargetAs(Hook hook) {
		if(!(hook instanceof ClassHook))
			return false;
		ClassHook classHook = (ClassHook) hook;
		return superclassName.equals(classHook.getSuperclassName());
	}
}
