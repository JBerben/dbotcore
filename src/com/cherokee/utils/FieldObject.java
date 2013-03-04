/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cherokee.utils;

import java.util.Properties;

import java.io.*;
import java.lang.reflect.Field;

import org.darkstorm.bcel.deobbers.EuclideanNumberDeobber.EuclideanNumberPair;
import org.darkstorm.runescape.oldschool.MMIRepository;

import com.cherokee.utils.ArrayWrapper.ArrayType;

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
public class FieldObject {

	private Field field;
	private Object parent;
	private boolean isStatic;
	private boolean isMutable = false;
	private boolean hasBackReference = false;
	private String parentName;
	private static Properties altNames = new Properties();

	public static boolean loadAltNames(File file) {
		try {
			altNames.load(new FileInputStream(file));
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public static boolean saveAltNames(File file) {
		try {
			altNames.store(new FileOutputStream(file), "ReflectionBuddy Dump.");
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public FieldObject(Field field, Object parent, String parentName,
			boolean isStatic) {
		// none of the required fields can be null
		if(field == null || parentName == null)
			return;
		// parent may only be null if the field is static
		if(!isStatic && parent == null)
			return;
		this.field = field;
		this.parent = parent;
		this.parentName = parentName;
		this.isStatic = isStatic;

		if(field.getType().isPrimitive()
				|| field.getType().getName().equals("java.lang.String"))
			isMutable = true;
	}

	public String getAltName() {
		return altNames.getProperty(parentName + "." + field.getName());
	}

	public String getParentName() {
		return parentName;
	}

	public boolean isMutable() {
		return isMutable;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isArray() {
		return field.getType().isArray();
	}

	public Object getParent() {
		return parent;
	}

	public void setAltName(String altName) {
		altNames.setProperty(parentName + "." + field.getName(), altName);
	}

	public boolean setValue(String newValue) {
		// isMutable enforces that this is one of the checked types
		if(!isMutable || newValue == null)
			return false;

		Object newVal = null;

		String type = field.getType().getName();
		// can't switch on a string in Java, annoying...
		try {
			if(type.equals("java.lang.String"))
				newVal = newValue;
			else if(type.equals("int"))
				newVal = Integer.parseInt(newValue);
			else if(type.equals("boolean"))
				newVal = Boolean.parseBoolean(newValue);
			else if(type.equals("byte"))
				newVal = Byte.parseByte(newValue);
			else if(type.equals("short"))
				newVal = Short.parseShort(newValue);
			else if(type.equals("double"))
				newVal = Double.parseDouble(newValue);
			else if(type.equals("float"))
				newVal = Float.parseFloat(newValue);
			else if(type.equals("long"))
				newVal = Long.parseLong(newValue);
			else if(type.equals("char")) {
				// make sure the string is at least 1 character
				if(newValue.length() < 1)
					return false;
				newVal = newValue.charAt(0);
			}

			// at this point, we want to set this field to the value in newVal
			boolean accessible = field.isAccessible();
			if(!accessible)
				field.setAccessible(true);

			field.set(parent, newVal);

			if(!accessible)
				field.setAccessible(false);

			return true;

		} catch(Exception e) {
			// they didn't enter a valid value, or the set failed
			return false;
		}
	}

	public Object getValue() {
		boolean accessible = field.isAccessible();
		if(!accessible)
			field.setAccessible(true);

		Object val;
		try {
			val = field.get(parent);
		} catch(Exception e) {
			// should never get here
			e.printStackTrace();
			val = null;
		}

		if(!accessible)
			field.setAccessible(false);

		return val;
	}

	public boolean hasBackReference() {
		return hasBackReference;
	}

	public void setHasBackReference(boolean hasBackreference) {
		hasBackReference = hasBackreference;
	}

	public String getType() {
		if(!field.getType().isArray())
			return field.getType().getName();

		Object value = getValue();
		if(value != null)
			return new ArrayWrapper(value).toString();
		return ArrayType.getType(field.getType()).toString(field.getType());
	}

	public String getArray() {
		if(!field.getType().isArray())
			return "Not Array";

		StringBuffer s = new StringBuffer();
		try {
			concatArray(getValue(), 0, s);
		} catch(IllegalArgumentException exception) {
			exception.printStackTrace();
		} catch(IllegalAccessException exception) {
			exception.printStackTrace();
		} catch(NoSuchFieldException exception) {
			exception.printStackTrace();
		} catch(SecurityException exception) {
			exception.printStackTrace();
		}
		// remove last ', '
		return s.toString().substring(0, s.length() - 2);
	}

	private void concatArray(Object arr, int indent, StringBuffer s)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException {
		if(arr == null) {
			s.append(toIndents(indent)).append("null\n");
			return;
		}
		s.append(toIndents(indent)).append("{");
		try {
			String type = arr.getClass().getName();
			int dimensions = type.lastIndexOf("[") + 1;
			if(dimensions > 1) {
				s.append(" [").append(((Object[]) arr).length).append("]\n");
				for(Object o : (Object[]) arr)
					concatArray(o, indent + 1, s);
				return;
			}

			// This is the only way I see for doing this, but it's nasty.
			// Please give me another idea if you have one.
			if(type.equals("[Z")) {
				boolean[] array = (boolean[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[B")) {
				byte[] array = (byte[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[C")) {
				char[] array = (char[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[D")) {
				double[] array = (double[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[F")) {
				float[] array = (float[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[I")) {
				int[] array = (int[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[J")) {
				long[] array = (long[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.equals("[S")) {
				short[] array = (short[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			} else if(type.contains("[L")) {
				Object[] array = (Object[]) arr;
				s.append(" [").append(array.length).append("]\n");
				for(int x = 0; x < array.length; ++x) {
					s.append(toIndents(indent + 1));
					s.append(array[x]);
					s.append(", ");
					s.append("\n");
				}
			}
		} finally {
			s.append(toIndents(indent)).append("},\n");
		}
	}

	private String toIndents(int indentCount) {
		StringBuffer indents = new StringBuffer();
		for(int i = 0; i < indentCount; i++)
			indents.append("  ");
		return indents.toString();
	}

	public Field getField() {
		return field;
	}

	@Override
	public String toString() {
		String altName = getAltName();
		return (isStatic ? "static " : "") + getType() + " " + parentName + "."
				+ ((altName == null) ? field.getName() : altName)
				+ (hasBackReference ? " (backreferenced)" : "");
	}

	public String getFullString() {
		String altName = getAltName();
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name:\n").append(field.getName());
		if(altName != null)
			buffer.append("\nRenamed:\n").append(altName);
		buffer.append("\nType:\n");
		if(isStatic)
			buffer.append("static ");
		buffer.append(getType());
		if(!field.getType().isArray()) {
			buffer.append("\nValue:\n");
			if(field.getType().equals(Integer.TYPE)) {
				int value = (Integer) getValue();
				buffer.append(value);
				EuclideanNumberPair pair = MMIRepository.getPair(field
						.getDeclaringClass().getName(), field.getName());
				if(pair != null) {
					buffer.append(value).append("\nDemultiplied:");
					buffer.append("\nP: ").append(pair.product());
					buffer.append("\n= ").append(
							value * pair.product().intValue());
					buffer.append("\nQ: ").append(pair.quotient());
					buffer.append("\n= ").append(
							value * pair.quotient().intValue());
					if(pair.isUnsafe())
						buffer.append(" (unsafe)");
				}
			} else if(field.getType().equals(Long.TYPE)) {
				long value = (Long) getValue();
				buffer.append(value);
				EuclideanNumberPair pair = MMIRepository.getPair(field
						.getDeclaringClass().getName(), field.getName());
				if(pair != null) {
					buffer.append(value).append("\nDemultiplied:");
					buffer.append("\nP: ").append(pair.product());
					buffer.append("\n= ").append(
							value * pair.product().longValue());
					buffer.append("\nQ: ").append(pair.quotient());
					buffer.append("\n= ").append(
							value * pair.quotient().longValue());
					if(pair.isUnsafe())
						buffer.append(" (unsafe)");
				}
			} else
				buffer.append(getValue());
		}
		return buffer.toString();
	}
}
