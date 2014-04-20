package org.darkstorm.runescape.util;

import java.io.*;
import java.net.*;
import java.security.ProtectionDomain;
import java.util.*;

public class CustomClassLoader extends URLClassLoader {
	private Map<String, byte[]> classes = new HashMap<String, byte[]>();
	private Map<String, byte[]> resources = new HashMap<String, byte[]>();
	private ProtectionDomain domain;

	public CustomClassLoader() {
		this(null);
	}

	public CustomClassLoader(ProtectionDomain domain) {
		super(new URL[0]);
		this.domain = domain;
	}

	public void addClass(String name, byte[] data) {
		synchronized(classes) {
			classes.put(name, data);
		}
	}

	public void addResource(String name, byte[] data) {
		synchronized(resources) {
			resources.put(name, data);
		}
	}

	public void clearData() {
		synchronized(classes) {
			classes.clear();
		}
		synchronized(resources) {
			resources.clear();
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if(classes.containsKey(name)) {
			byte[] data;
			synchronized(classes) {
				data = classes.get(name);
			}
			return defineClass(name, data, 0, data.length, domain);
		}
		return super.findClass(name);
	}

	@Override
	public URL findResource(String name) {
		if(resources.containsKey(name)) {
			synchronized(resources) {
				final byte[] data = resources.get(name);
				try {
					URL url = new URL(new URL("file:/asdf"), name,
							new URLStreamHandler() {

								@Override
								protected URLConnection openConnection(URL u)
										throws IOException {
									URLConnection connection = new URLConnection(
											u) {
										private ByteArrayInputStream in;

										@Override
										public void connect()
												throws IOException {
											in = new ByteArrayInputStream(data);
										}

										@Override
										public InputStream getInputStream()
												throws IOException {
											return in;
										}
									};
									connection.connect();
									return connection;
								}
							});
					return url;
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		return super.findResource(name);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
}