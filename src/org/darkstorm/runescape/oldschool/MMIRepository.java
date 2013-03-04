package org.darkstorm.runescape.oldschool;

import java.util.*;
import java.util.concurrent.locks.*;

import java.lang.reflect.Field;
import java.math.BigInteger;

import org.darkstorm.bcel.deobbers.EuclideanNumberDeobber.EuclideanNumberPair;

/**
 * Cache of modular multiplicative inverse keys used by the client to cipher int/long fields
 * 
 */
public final class MMIRepository {
	private static Map<String, EuclideanNumberPair> pairs;
	private static final Lock lock = new ReentrantLock();

	private MMIRepository() {
		throw new UnsupportedOperationException();
	}

	static void init(Map<String, EuclideanNumberPair> pairs) {
		lock.lock();
		if(MMIRepository.pairs != null)
			throw new IllegalStateException();
		MMIRepository.pairs = Collections.unmodifiableMap(pairs);
		lock.unlock();
	}

	// Products
	public static long calculateProduct(Class<?> c, Field field, long value) {
		return calculateProduct(c.getName(), field.getName(), value);
	}

	public static long calculateProduct(String className, String fieldName,
			long value) {
		return calculateProduct(className + "." + fieldName, value);
	}

	public static long calculateProduct(String key, long value) {
		EuclideanNumberPair pair = getPair(key);
		if(pair == null)
			return value;
		return pair.product().multiply(BigInteger.valueOf(value)).longValue();
	}

	public static int calculateProduct(Class<?> c, Field field, int value) {
		return calculateProduct(c.getName(), field.getName(), value);
	}

	public static int calculateProduct(String className, String fieldName,
			int value) {
		return calculateProduct(className + "." + fieldName, value);
	}

	public static int calculateProduct(String key, int value) {
		EuclideanNumberPair pair = getPair(key);
		if(pair == null)
			return value;
		return pair.product().multiply(BigInteger.valueOf(value)).intValue();
	}

	// Quotients
	public static long calculateQuotient(Class<?> c, Field field, long value) {
		return calculateQuotient(c.getName(), field.getName(), value);
	}

	public static long calculateQuotient(String className, String fieldName,
			long value) {
		return calculateQuotient(className + "." + fieldName, value);
	}

	public static long calculateQuotient(String key, long value) {
		EuclideanNumberPair pair = getPair(key);
		if(pair == null)
			return value;
		return pair.quotient().multiply(BigInteger.valueOf(value)).longValue();
	}

	public static int calculateQuotient(Class<?> c, Field field, int value) {
		return calculateQuotient(c.getName(), field.getName(), value);
	}

	public static int calculateQuotient(String className, String fieldName,
			int value) {
		return calculateQuotient(className + "." + fieldName, value);
	}

	public static int calculateQuotient(String key, int value) {
		EuclideanNumberPair pair = getPair(key);
		if(pair == null)
			return value;
		return pair.quotient().multiply(BigInteger.valueOf(value)).intValue();
	}

	// Pair getters
	public static EuclideanNumberPair getPair(Class<?> c, Field field) {
		return getPair(c.getName(), field.getName());
	}

	public static EuclideanNumberPair getPair(String className, String fieldName) {
		return getPair(className + "." + fieldName);
	}

	public static EuclideanNumberPair getPair(String key) {
		return pairs.get(key);
	}
}
