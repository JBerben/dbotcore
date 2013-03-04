package org.darkstorm.runescape.util;

import java.util.*;
import java.util.jar.*;

import java.awt.AWTPermission;
import java.io.*;
import java.net.*;
import java.security.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

/**
 * Created by IntelliJ IDEA. User: Jan Ove / Kosaki Date: 18.mar.2009 Time:
 * 11:17:04
 */
public class CustomClassLoader extends ClassLoader {
	private final Map<String, byte[]> classes = new HashMap<String, byte[]>();
	private ProtectionDomain domain;

	public CustomClassLoader(byte[] data) {
		this(loadClasses(data));
	}

	public CustomClassLoader(ClassGen[] classes) {
		try {
			CodeSource codeSource = new CodeSource(new URL(
					"http://runescape.com/"), (CodeSigner[]) null);
			domain = new ProtectionDomain(codeSource, getPermissions());
			for(ClassGen classGen : classes)
				this.classes.put(classGen.getClassName(), classGen
						.getJavaClass().getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static ClassGen[] loadClasses(byte[] data) {
		try {
			List<ClassGen> classes = new ArrayList<ClassGen>();
			JarInputStream in = new JarInputStream(new ByteArrayInputStream(
					data));
			JarEntry entry;
			while((entry = in.getNextJarEntry()) != null) {
				String name = entry.getName();
				if(name.endsWith(".class")) {
					JavaClass javaClass = new ClassParser(in, name).parse();
					ClassGen classGen = new ClassGen(javaClass);
					if(classGen.getConstantPool().lookupString("random.dat") != -1) {
						/**
						 * Ollies UID hack.
						 */
						int random = (int) (Math.random() * Integer.MAX_VALUE);
						ConstantPoolGen cpg = classGen.getConstantPool();
						((ConstantString) cpg.getConstant(cpg
								.lookupString("random.dat")))
								.setStringIndex(cpg.addUtf8("random-" + random
										+ ".dat"));
						System.out.println("replaced random.dat in " + name);
						javaClass = classGen.getJavaClass();
					}
					classes.add(classGen);
				}
			}
			return classes.toArray(new ClassGen[classes.size()]);
		} catch(IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	private Permissions getPermissions() {
		Permissions ps = new Permissions();
		ps.add(new AWTPermission("accessEventQueue"));
		ps.add(new PropertyPermission("user.home", "read"));
		ps.add(new PropertyPermission("java.vendor", "read"));
		ps.add(new PropertyPermission("java.version", "read"));
		ps.add(new PropertyPermission("os.name", "read"));
		ps.add(new PropertyPermission("os.arch", "read"));
		ps.add(new PropertyPermission("os.version", "read"));
		ps.add(new SocketPermission("*", "connect,resolve"));
		String uDir = System.getProperty("user.home");
		if(uDir != null) {
			uDir += "/";
		} else {
			uDir = "~/";
		}
		String[] dirs = { "c:/rscache/", "/rscache/", "c:/windows/",
				"c:/winnt/", "c:/", uDir, "/tmp/", "." };
		String[] rsDirs = { ".jagex_cache_32", ".file_store_32" };
		for(String dir : dirs) {
			File f = new File(dir);
			ps.add(new FilePermission(dir, "read"));
			if(!f.exists())
				continue;
			dir = f.getPath();
			for(String rsDir : rsDirs) {
				ps.add(new FilePermission(dir + File.separator + rsDir
						+ File.separator + "-", "read"));
				ps.add(new FilePermission(dir + File.separator + rsDir
						+ File.separator + "-", "write"));
			}
		}
		ps.setReadOnly();
		return ps;
	}

	@Override
	public final Class<?> loadClass(String name) throws ClassNotFoundException {
		if(classes.containsKey(name)) {
			byte buffer[] = classes.remove(name);
			return defineClass(name, buffer, 0, buffer.length, domain);
		}
		return super.loadClass(name);
	}

	/**
	 * Returns all of the <tt>Packages</tt> defined by this class loader and its
	 * ancestors. </p>
	 * 
	 * @return The array of <tt>Package</tt> objects defined by this
	 *         <tt>ClassLoader</tt>
	 * @since 1.2
	 */
	@Override
	protected Package[] getPackages() {
		System.out.println("Requested packages.");
		return super.getPackages();
	}
}
