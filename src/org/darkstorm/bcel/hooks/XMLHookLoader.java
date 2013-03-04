package org.darkstorm.bcel.hooks;

import java.util.*;

import java.io.StringReader;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

public class XMLHookLoader implements HookLoader {
	private final String xml;

	private Hook[] hooks;
	private String dataVersion;

	public XMLHookLoader(String xml) {
		this.xml = xml;
	}

	@Override
	public void load() {
		Document document = loadXML();
		hooks = parseHooks(document);
	}

	private Document loadXML() {
		try {
			return new SAXBuilder().build(new StringReader(xml));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Hook[] parseHooks(Document document) {
		List<Hook> hooks = new ArrayList<Hook>();
		Element rootElement = document.getRootElement();
		dataVersion = rootElement.getAttributeValue("version");
		List<Element> hookElements = rootElement.getChildren();
		for(Element element : hookElements) {
			String type = element.getName();
			if(type.equals("interface")) {
				hooks.add(new InterfaceHook(element));
				for(Element interfaceElement : (List<Element>) element
						.getChildren()) {
					type = interfaceElement.getName();
					if(type.equals("getter"))
						hooks.add(new GetterHook(interfaceElement));
					else if(type.equals("setter"))
						hooks.add(new SetterHook(interfaceElement));
					else if(type.equals("method"))
						hooks.add(new MethodHook(interfaceElement));
					else if(type.equals("field"))
						hooks.add(new FieldHook(interfaceElement));
				}
			} else if(type.equals("callback"))
				hooks.add(new CallbackHook(element));
			else if(type.equals("bytecode"))
				hooks.add(new BytecodeHook(element));
			else if(type.equals("class"))
				hooks.add(new ClassHook(element));
		}
		return hooks.toArray(new Hook[hooks.size()]);
	}

	@Override
	public Hook[] getHooks() {
		return hooks.clone();
	}

	@Override
	public String getDataVersion() {
		return dataVersion;
	}
}