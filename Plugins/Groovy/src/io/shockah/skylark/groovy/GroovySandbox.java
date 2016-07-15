package io.shockah.skylark.groovy;

import java.net.URL;
import java.util.List;
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
			})).build();
	
	private static final ImmutableList<String> PACKAGE_WHITELIST = ImmutableList.copyOf(new String[] {
		"java.util", "java.math", "java.text"
	});
	
	private static final ImmutableList<Class<?>> CLASS_WHITELIST = ImmutableList.copyOf(new Class<?>[] {
		Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, String.class,
		Object.class, Number.class, StringBuilder.class, Math.class, URL.class,
		Random.class,
		HttpRequest.class
	});
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
		if (CLASS_BLACKLIST.contains(receiver))
			throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getName(), method));
		
		for (String packagePrefix : PACKAGE_WHITELIST) {
			if (receiver.getName().startsWith(packagePrefix + "."))
				return super.onStaticCall(invoker, receiver, method, args);
		}
		
		if (CLASS_WHITELIST.contains(receiver))
			return super.onStaticCall(invoker, receiver, method, args);
		
		throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getName(), method));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
		if (CLASS_BLACKLIST.contains(receiver))
			throw new SecurityException(String.format("%s constructor call not allowed.", receiver.getName()));
		
		for (String packagePrefix : PACKAGE_WHITELIST) {
			if (receiver.getName().startsWith(packagePrefix + "."))
				return super.onNewInstance(invoker, receiver, args);
		}
		
		if (CLASS_WHITELIST.contains(receiver))
			return super.onNewInstance(invoker, receiver, args);
		
		throw new SecurityException(String.format("%s constructor call not allowed.", receiver.getName()));
	}
	
	@Override
	public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
		Class<?> clazz = receiver.getClass();
		do {
			if (CLASS_BLACKLIST.contains(clazz))
				throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
			
			List<String> methods = METHOD_BLACKLIST.get(clazz);
			if (methods != null && methods.contains(method))
				throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
			
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return super.onMethodCall(invoker, receiver, method, args);
	}
	
	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
		String method = String.format("get%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
		
		Class<?> clazz = receiver.getClass();
		do {
			if (CLASS_BLACKLIST.contains(clazz))
				throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
			
			List<String> methods = METHOD_BLACKLIST.get(clazz);
			if (methods != null && methods.contains(method))
				throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
			
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return super.onGetProperty(invoker, receiver, property);
	}
	
	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
		String method = String.format("set%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
		
		Class<?> clazz = receiver.getClass();
		do {
			if (CLASS_BLACKLIST.contains(clazz))
				throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
			
			List<String> methods = METHOD_BLACKLIST.get(clazz);
			if (methods != null && methods.contains(method))
				throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getClass().getName(), method));
			
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		
		return super.onSetProperty(invoker, receiver, property, value);
	}
}