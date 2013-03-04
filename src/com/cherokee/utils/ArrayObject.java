package com.cherokee.utils;

public class ArrayObject {
	private final FieldObject source;
	private final ArrayWrapper array;
	private final Object object;
	private final int index;

	public ArrayObject(FieldObject source, ArrayWrapper array, Object object,
			int index) {
		this.source = source;
		this.array = array;
		this.object = object;
		this.index = index;
	}

	public FieldObject getSource() {
		return source;
	}

	public ArrayWrapper getArray() {
		return array;
	}

	public int getIndex() {
		return index;
	}

	public Object getObject() {
		return object;
	}

	@Override
	public String toString() {
		if(object.getClass().isArray())
			return "[" + index + "] " + new ArrayWrapper(object).toString();
		return "[" + index + "] "
				+ array.getArray().getClass().getComponentType().getName();
	}
}
