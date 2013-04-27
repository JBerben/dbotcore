package com.cherokee.utils;

import java.lang.reflect.*;

import javax.swing.tree.DefaultMutableTreeNode;

/* 
 * Copyright © 2008  Travis Burtrum (moparisthebest)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * The code *may* be used under a lesser license (such as the LGPL) only with
 * express written permission from Travis Burtrum (moparisthebest)
 */
public class RuntimeViewer {

	public static void getFields(Object o, DefaultMutableTreeNode root) {
		String name = o.getClass().getName();
		root.setUserObject(name);
		getFields(name, o.getClass(), o, root, 3);
	}

	public static void getFields(Class<?> c, DefaultMutableTreeNode root) {
		String name = c.getName();
		root.setUserObject(name);
		getFields(name, c, null, root, 3);
	}

	public static void getFields(String name, Class<?> klass, Object o,
			DefaultMutableTreeNode parent, int maxDepth) {
		if(maxDepth < 0)
			return;
		Field[] all = klass.getDeclaredFields();
		// if there are no fields, add nothing to the tree
		if(all.length == 0)
			return;
		for(Field f : all) {
			boolean isStatic = Modifier.isStatic(f.getModifiers());
			// if o is static, we can only look at static members of this class
			if(o == null && !isStatic)
				continue;
			FieldObject fo = new FieldObject(f, o, (isStatic ? klass.getName()
					: name), isStatic);
			Object value = fo.getValue();
			if(value == o) {
				if(parent.getUserObject() instanceof FieldObject)
					((FieldObject) parent.getUserObject())
							.setHasBackReference(true);
				else if(parent.getUserObject() instanceof String)
					parent.setUserObject(parent.getUserObject()
							+ " (backreferenced)");
				continue;
			}
			DefaultMutableTreeNode branch = new DefaultMutableTreeNode(fo);
			// assures the value is primitive or a class provided by sun before
			// displaying the value
			if(f.getType().isPrimitive()
					|| f.getType().isInterface()
					|| f.getType().getName().startsWith("org.darkstorm")
					|| (f.getType().getPackage() != null && f.getType()
							.getPackage().getImplementationVendor() != null)) {
				parent.add(branch);
				// if its another type, recurse on it to display it's values
			} else if(f.getType().isArray() && value != null) {
				parent.add(branch);
				getFieldsFromArray(f, fo, value, branch, maxDepth - 1);
			} else {
				parent.add(branch);
				Class<?> superclass = f.getType().getSuperclass();
				if(!(superclass == null || superclass.isPrimitive()
						|| superclass.isArray()
						|| superclass.equals(Object.class) || (superclass
						.getPackage() != null && superclass.getPackage()
						.getImplementationVendor() != null))) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							"*Superclass " + superclass.getName());
					branch.add(node);
					getFields(f.getName(), superclass, value, node,
							Math.min(maxDepth - 1, 1));
				}
				getFields(f.getName(), f.getType(), value, branch, maxDepth - 1);
			}
		}
	}

	public static void getFieldsFromArray(Field f, FieldObject source,
			Object array, DefaultMutableTreeNode parent, int maxDepth) {
		if(maxDepth < 0)
			return;
		ArrayWrapper wrapper = new ArrayWrapper(array);
		for(int i = 0; i < wrapper.length(); i++) {
			Object object = wrapper.get(i);
			if(object == null)
				continue;
			ArrayObject ao = new ArrayObject(source, wrapper, object, i);
			if(object.getClass().isPrimitive()
					|| (object.getClass().getPackage() != null && object
							.getClass().getPackage().getImplementationVendor() != null)) {
				parent.add(new DefaultMutableTreeNode(ao));
				// if its another type, recurse on it to display it's values
			} else if(object.getClass().isArray()) {
				DefaultMutableTreeNode branch = new DefaultMutableTreeNode(ao);
				parent.add(branch);
				getFieldsFromArray(f, source, object, branch, maxDepth - 1);
			} else {
				DefaultMutableTreeNode branch = new DefaultMutableTreeNode(ao);
				parent.add(branch);
				getFields(f.getName(), object.getClass(), object, branch,
						maxDepth - 1);
			}
		}
	}
}