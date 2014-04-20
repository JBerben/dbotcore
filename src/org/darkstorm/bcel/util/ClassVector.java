package org.darkstorm.bcel.util;

import java.util.Vector;

import org.apache.bcel.generic.ClassGen;
import org.darkstorm.bcel.Updater;
import org.darkstorm.bcel.hooks.*;
import org.darkstorm.bcel.transformers.Transformer;

public class ClassVector extends Vector<ClassGen> {
	private static final long serialVersionUID = 8663919436007878267L;

	public ClassVector() {
	}

	public ClassGen getByName(String name) {
		if(name == null)
			return null;
		for(Object o : elementData) {
			if(o != null) {
				ClassGen cg = (ClassGen) o;
				if(cg.getClassName().equals(name))
					return cg;
			}
		}
		return null;
	}

	public boolean containsByName(String name) {
		if(name == null)
			return false;
		for(Object o : elementData) {
			if(o != null) {
				ClassGen cg = (ClassGen) o;
				if(cg.getClassName().equals(name))
					return true;
			}
		}
		return false;
	}

	public ClassGen getByInterface(Updater updater, String interfaceName) {
		if(interfaceName == null)
			return null;
		if(!interfaceName.startsWith(updater.getHooksPackage()))
			interfaceName = updater.getHooksPackage() + interfaceName;
		String hookClass = null;
		for(Transformer transformer : updater.getTransformers()) {
			for(Hook hook : transformer.getHooks()) {
				if(hook instanceof InterfaceHook) {
					InterfaceHook interfaceHook = (InterfaceHook) hook;
					String hookInterfaceName = interfaceHook.getInterfaceName();
					if(interfaceName.equals(hookInterfaceName))
						hookClass = interfaceHook.getClassName();
				}
			}
		}
		for(Object o : elementData) {
			if(o != null) {
				ClassGen cg = (ClassGen) o;
				if(hookClass != null && cg.getClassName().equals(hookClass))
					return cg;
				for(String classGenInterfaceName : cg.getInterfaceNames())
					if(classGenInterfaceName.endsWith(interfaceName))
						return cg;
			}
		}
		return null;
	}

	public ClassGen getByInterface(String interfaceName) {
		if(interfaceName == null)
			return null;
		for(ClassGen classGen : this)
			for(String classInterfaceName : classGen.getInterfaceNames())
				if(interfaceName.equals(classInterfaceName))
					return classGen;
		return null;
	}
}
