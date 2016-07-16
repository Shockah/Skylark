package io.shockah.skylark.groovy;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class GroovySandbox extends GroovyInterceptor {
	private static final ImmutableList<Class<?>> CLASS_BLACKLIST = ImmutableList.copyOf(new Class<?>[] {
		System.class, Class.class
	});
	
	private static final ImmutableMap<Class<?>, ImmutableList<String>> METHOD_BLACKLIST = ImmutableMap.<Class<?>, ImmutableList<String>>builder()
			.put(Object.class, ImmutableList.copyOf(new String[] {
					"getClass", "wait", "notify", "notifyAll", "finalize"
			}))
			.build();
	
	private static final ImmutableList<String> PACKAGE_WHITELIST = ImmutableList.copyOf(new String[] {
		"java.util", "java.math", "java.text",
		"io.shockah.skylark", "io.shockah.json"
	});
	
	private static final ImmutableList<Class<?>> CLASS_WHITELIST = ImmutableList.copyOf(new Class<?>[] {
		Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, String.class,
		Object.class, Number.class, StringBuilder.class, Math.class, URL.class,
		Random.class,
		HttpRequest.class
	});
	
	private static final ImmutableMap<Class<?>, ImmutableList<String>> METHOD_WHITELIST = ImmutableMap.<Class<?>, ImmutableList<String>>builder()
			.build();
	
	protected final List<Class<?>> classBlacklist = new ArrayList<>(CLASS_BLACKLIST);
	protected final Map<Class<?>, List<String>> methodBlacklist = new HashMap<>(METHOD_BLACKLIST);
	protected final List<String> packageWhitelist = new ArrayList<>(PACKAGE_WHITELIST);
	protected final List<Class<?>> classWhitelist = new ArrayList<>(CLASS_WHITELIST);
	protected final Map<Class<?>, List<String>> methodWhitelist = new HashMap<>(METHOD_WHITELIST);
	
	public GroovySandbox addBlacklistedClasses(Class<?>... classes) {
		for (Class<?> clazz : classes)
			classBlacklist.add(clazz);
		return this;
	}
	
	public GroovySandbox addBlacklistedMethods(Class<?> clazz, String... methods) {
		List<String> list = methodBlacklist.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			methodBlacklist.put(clazz, list);
		}
		for (String method : methods)
			list.add(method);
		return this;
	}
	
	public GroovySandbox addWhitelistedPackages(String... packages) {
		for (String pack : packages)
			packageWhitelist.add(pack);
		return this;
	}
	
	public GroovySandbox addWhitelistedClasses(Class<?>... classes) {
		for (Class<?> clazz : classes)
			classWhitelist.add(clazz);
		return this;
	}
	
	public GroovySandbox addWhitelistedMethods(Class<?> clazz, String... methods) {
		List<String> list = methodWhitelist.get(clazz);
		if (list == null) {
			list = new ArrayList<>();
			methodWhitelist.put(clazz, list);
		}
		for (String method : methods)
			list.add(method);
		return this;
	}
	
	public boolean isStaticCallAllowed(Class<?> receiver, String method, Object... args) {
		if (classBlacklist.contains(receiver))
			return false;
		
		for (String packagePrefix : packageWhitelist) {
			if (receiver.getName().startsWith(packagePrefix + "."))
				return true;
		}
		
		if (classWhitelist.contains(receiver))
			return true;
		
		return false;
	}
	
	public boolean isCallAllowed(Object receiver, String method, Object... args) {
		Class<?> clazz = receiver.getClass();
		boolean whitelistedMethod = false;
		do {
			if (classBlacklist.contains(clazz))
				return false;
			
			List<String> methods = methodBlacklist.get(clazz);
			if (methods != null && methods.contains(method))
				return false;
			
			if (!whitelistedMethod) {
				methods = methodWhitelist.get(clazz);
				if (methods != null) {
					if (methods.contains(method))
						whitelistedMethod = true;
					else
						return false;
				}
			}
			
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
		if (isStaticCallAllowed(receiver, method, args))
			return super.onStaticCall(invoker, receiver, method, args);
		else
			throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getName(), method));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
		if (isStaticCallAllowed(receiver, "..ctor", args))
			return super.onNewInstance(invoker, receiver, args);
		else
			throw new SecurityException(String.format("%s constructor call not allowed.", receiver.getName()));
	}
	
	@Override
	public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
		if (isCallAllowed(receiver, method, args))
			return super.onMethodCall(invoker, receiver, method, args);
		else
			throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
	}
	
	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
		String method = String.format("get%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
		if (isCallAllowed(receiver, method))
			return super.onMethodCall(invoker, receiver, property);
		else
			throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
	}
	
	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
		String method = String.format("set%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
		if (isCallAllowed(receiver, method, value))
			return super.onMethodCall(invoker, receiver, property, value);
		else
			throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
	}
}