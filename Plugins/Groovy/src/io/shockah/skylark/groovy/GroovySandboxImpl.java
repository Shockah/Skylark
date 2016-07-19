package io.shockah.skylark.groovy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class GroovySandboxImpl extends AbstractGroovySandbox {
	private static final List<Class<?>> CLASS_BLACKLIST = ImmutableList.<Class<?>>builder().add(
		System.class
	).build();
	
	private static final Map<Class<?>, List<String>> METHOD_BLACKLIST = ImmutableMap.<Class<?>, List<String>>builder().put(
		Object.class, ImmutableList.<String>builder().add(
			"wait", "notify", "notifyAll", "finalize"
		).build()
	).put(
		Class.class, ImmutableList.<String>builder().add(
			"forName", "getClassLoader", "getResource", "newInstance"
		).build()
	).put(
		Constructor.class, ImmutableList.<String>builder().add(
			"newInstance"
		).build()
	).put(
		Method.class, ImmutableList.<String>builder().add(
			"invoke"
		).build()
	).put(
		Field.class, ImmutableList.<String>builder().add(
			"get", "getBoolean", "getByte", "getChar", "getDouble", "getFloat", "getInt", "getLong", "getShort",
			"set", "setBoolean", "setByte", "setChar", "setDouble", "setFloat", "setInt", "setLong", "setShort"
		).build()
	).build();
	
	private static final Map<Class<?>, List<String>> FIELD_BLACKLIST = ImmutableMap.<Class<?>, List<String>>builder().build();
	
	private static final List<String> PACKAGE_WHITELIST = ImmutableList.<String>builder().add(
		"io.shockah.json",
		"java.math", "java.text", "java.util"
	).build();
	
	private static final List<Class<?>> CLASS_WHITELIST = ImmutableList.<Class<?>>builder().add(
		Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
		Character.class, String.class, Object.class, Number.class, StringBuilder.class, Math.class,
		HttpRequest.class
	).build();
	
	private static final Map<Class<?>, List<String>> METHOD_WHITELIST = ImmutableMap.<Class<?>, List<String>>builder().build();
	
	protected final List<Class<?>> classBlacklist = new ArrayList<>(CLASS_BLACKLIST);
	protected final Map<Class<?>, List<String>> methodBlacklist = new HashMap<>(METHOD_BLACKLIST);
	protected final Map<Class<?>, List<String>> fieldBlacklist = new HashMap<>(FIELD_BLACKLIST);
	protected final List<String> packageWhitelist = new ArrayList<>(PACKAGE_WHITELIST);
	protected final List<Class<?>> classWhitelist = new ArrayList<>(CLASS_WHITELIST);
	protected final Map<Class<?>, List<String>> methodWhitelist = new HashMap<>(METHOD_WHITELIST);
	
	protected final boolean isClassBlacklisted(Class<?> clazz) {
		do {
			if (classBlacklist.contains(clazz))
				return true;
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return false;
	}
	
	protected final boolean isMethodBlacklisted(Class<?> clazz, String method) {
		do {
			List<String> methods = methodBlacklist.get(clazz);
			if (methods != null && methods.contains(method))
				return true;
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return false;
	}
	
	protected final boolean isPackageWhitelisted(Class<?> clazz) {
		for (String packagePrefix : packageWhitelist) {
			if (clazz.getName().startsWith(packagePrefix + "."))
				return true;
		}
		return false;
	}
	
	protected final boolean isClassWhitelisted(Class<?> clazz) {
		return classWhitelist.contains(clazz);
		/*do {
			if (classWhitelist.contains(clazz))
				return true;
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return false;*/
	}
	
	protected final Boolean isMethodWhitelisted(Class<?> clazz, String method) {
		do {
			List<String> methods = methodWhitelist.get(clazz);
			if (methods != null) {
				boolean drop = false;
				for (Method classMethod : clazz.getDeclaredMethods()) {
					if (classMethod.getName().equals(method)) {
						if (methods.contains(method))
							return true;
						else
							drop = true;
					}
				}
				if (drop)
					return false;
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return null;
	}
	
	@Override
	public boolean isInstanceMethodCallAllowed(Object obj, String method, Object... args) {
		if (isClassBlacklisted(obj.getClass()))
			return false;
		if (isMethodBlacklisted(obj.getClass(), method))
			return false;
		if (isPackageWhitelisted(obj.getClass()))
			return true;
		if (isClassWhitelisted(obj.getClass()))
			return true;
		
		Boolean methodWhitelisted = isMethodWhitelisted(obj.getClass(), method);
		if (methodWhitelisted != null)
			return methodWhitelisted;
		
		return true;
	}

	@Override
	public boolean isClassMethodCallAllowed(Class<?> clazz, String method, Object... args) {
		if (isClassBlacklisted(clazz))
			return false;
		if (isMethodBlacklisted(clazz, method))
			return false;
		if (isPackageWhitelisted(clazz))
			return true;
		if (isClassWhitelisted(clazz))
			return true;
		
		Boolean methodWhitelisted = isMethodWhitelisted(clazz, method);
		if (methodWhitelisted != null)
			return methodWhitelisted;
		
		return false;
	}

	@Override
	public boolean isConstructorAllowed(Class<?> clazz, Object... args) {
		if (isClassBlacklisted(clazz))
			return false;
		if (isPackageWhitelisted(clazz))
			return true;
		if (isClassWhitelisted(clazz))
			return true;
		return false;
	}

	@Override
	public boolean isInstanceFieldGetAllowed(Object obj, String field) {
		if (isClassBlacklisted(obj.getClass()))
			return false;
		/*if (isPackageWhitelisted(obj.getClass()))
			return true;
		if (isClassWhitelisted(obj.getClass()))
			return true;*/
		return true;
	}

	@Override
	public boolean isInstanceFieldSetAllowed(Object obj, String field, Object value) {
		if (isClassBlacklisted(obj.getClass()))
			return false;
		/*if (isPackageWhitelisted(obj.getClass()))
			return true;
		if (isClassWhitelisted(obj.getClass()))
			return true;*/
		return true;
	}

	@Override
	public boolean isClassFieldGetAllowed(Class<?> clazz, String field) {
		if (isClassBlacklisted(clazz))
			return false;
		/*if (isPackageWhitelisted(clazz))
			return true;
		if (isClassWhitelisted(clazz))
			return true;*/
		return true;
	}

	@Override
	public boolean isClassFieldSetAllowed(Class<?> clazz, String field, Object value) {
		if (isClassBlacklisted(clazz))
			return false;
		/*if (isPackageWhitelisted(clazz))
			return true;
		if (isClassWhitelisted(clazz))
			return true;*/
		return true;
	}
	
	public GroovySandboxImpl addBlacklistedClasses(Class<?>... classes) {
		for (Class<?> clazz : classes)
			classBlacklist.add(clazz);
		return this;
	}
	
	public GroovySandboxImpl addBlacklistedMethods(Class<?> clazz, String... methods) {
		List<String> list = methodBlacklist.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			methodBlacklist.put(clazz, list);
		}
		for (String method : methods)
			list.add(method);
		return this;
	}
	
	public GroovySandboxImpl addBlacklistedFields(Class<?> clazz, String... fields) {
		List<String> list = fieldBlacklist.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			fieldBlacklist.put(clazz, list);
		}
		for (String field : fields)
			list.add(field);
		return this;
	}
	
	public GroovySandboxImpl addWhitelistedPackages(String... packages) {
		for (String pack : packages)
			packageWhitelist.add(pack);
		return this;
	}
	
	public GroovySandboxImpl addWhitelistedClasses(Class<?>... classes) {
		for (Class<?> clazz : classes)
			classWhitelist.add(clazz);
		return this;
	}
	
	public GroovySandboxImpl addWhitelistedMethods(Class<?> clazz, String... methods) {
		List<String> list = methodWhitelist.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			methodWhitelist.put(clazz, list);
		}
		for (String method : methods)
			list.add(method);
		return this;
	}
}