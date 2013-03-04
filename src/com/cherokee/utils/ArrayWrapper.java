package com.cherokee.utils;

public class ArrayWrapper {
	private final ArrayType type;
	private final Object array;

	public ArrayWrapper(Object array) {
		if(array == null || !array.getClass().isArray())
			throw new IllegalArgumentException();
		type = ArrayType.getType(array);
		if(type == null)
			throw new IllegalArgumentException();
		this.array = array;
	}

	public Object get(int index) {
		return type.get(array, index);
	}

	public void set(Object value, int index) {
		type.set(array, value, index);
	}

	public int length() {
		return type.length(array);
	}

	public Object getArray() {
		return array;
	}

	@Override
	public String toString() {
		return type.toString(this);
	}

	public static enum ArrayType {
		OBJECT {
			@Override
			public Object get(Object array, int index) {
				return ((Object[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((Object[]) array)[index] = value;
			}

			@Override
			public String toString(Class<?> array) {
				Class<?> component = array.getComponentType();
				if(component.isArray())
					return ArrayType.getType(component).toString(component)
							+ "[]";
				return component.getName() + "[]";
			}

			@Override
			public int length(Object array) {
				return ((Object[]) array).length;
			}
		},
		BYTE {
			@Override
			public Object get(Object array, int index) {
				return ((byte[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((byte[]) array)[index] = (Byte) value;
			}

			@Override
			public int length(Object array) {
				return ((byte[]) array).length;
			}
		},
		BOOLEAN {
			@Override
			public Object get(Object array, int index) {
				return ((boolean[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((boolean[]) array)[index] = (Boolean) value;
			}

			@Override
			public int length(Object array) {
				return ((boolean[]) array).length;
			}
		},
		CHAR {
			@Override
			public Object get(Object array, int index) {
				return ((char[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((char[]) array)[index] = (Character) value;
			}

			@Override
			public int length(Object array) {
				return ((char[]) array).length;
			}
		},
		DOUBLE {
			@Override
			public Object get(Object array, int index) {
				return ((double[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((double[]) array)[index] = (Double) value;
			}

			@Override
			public int length(Object array) {
				return ((double[]) array).length;
			}
		},
		FLOAT {
			@Override
			public Object get(Object array, int index) {
				return ((float[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((float[]) array)[index] = (Float) value;
			}

			@Override
			public int length(Object array) {
				return ((float[]) array).length;
			}
		},
		INT {
			@Override
			public Object get(Object array, int index) {
				return ((int[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((int[]) array)[index] = (Integer) value;
			}

			@Override
			public int length(Object array) {
				return ((int[]) array).length;
			}
		},
		LONG {
			@Override
			public Object get(Object array, int index) {
				return ((long[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((long[]) array)[index] = (Long) value;
			}

			@Override
			public int length(Object array) {
				return ((long[]) array).length;
			}
		},
		SHORT {
			@Override
			public Object get(Object array, int index) {
				return ((short[]) array)[index];
			}

			@Override
			public void set(Object array, Object value, int index) {
				((short[]) array)[index] = (Short) value;
			}

			@Override
			public int length(Object array) {
				return ((short[]) array).length;
			}
		};

		private ArrayType() {
		}

		public abstract Object get(Object array, int index);

		public abstract void set(Object array, Object value, int index);

		public abstract int length(Object array);

		public String toString(ArrayWrapper array) {
			String stringValue = toString(array.getArray().getClass());
			return stringValue.substring(0, stringValue.length() - 2) + "["
					+ array.length() + "]";
		}

		public String toString(Class<?> array) {
			String type = array.getName();
			int lastBracket = type.lastIndexOf("[") + 1;
			String brackets = type.substring(0, lastBracket).replaceAll("\\[",
					"[]");
			return name().toLowerCase() + brackets;
		}

		public static ArrayType getType(Object array) {
			return getType(array.getClass());
		}

		public static ArrayType getType(Class<?> array) {
			if(!array.isArray())
				return null;
			String type = array.getName();
			int lastBracket = type.lastIndexOf("[") + 1;
			if(lastBracket > 1)
				return OBJECT;
			char t = type.charAt(lastBracket);
			switch(t) {
			case 'L':
				return OBJECT;
			case 'Z':
				return BOOLEAN;
			case 'B':
				return BYTE;
			case 'C':
				return CHAR;
			case 'D':
				return DOUBLE;
			case 'F':
				return FLOAT;
			case 'I':
				return INT;
			case 'J':
				return LONG;
			case 'S':
				return SHORT;
			}
			return null;
		}
	}
}
