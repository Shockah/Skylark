package io.shockah.skylark.groovy;

import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import com.google.common.collect.ImmutableList;

public class GroovySandbox extends GroovyInterceptor {
	private static final ImmutableList<Class<?>> CLASS_BLACKLIST = ImmutableList.copyOf(new Class<?>[] {
		System.class
	});
	
	private static final ImmutableList<Class<?>> CLASS_WHITELIST = ImmutableList.copyOf(new Class<?>[] {
		Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, String.class,
		Object.class, Number.class, StringBuilder.class
	});
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
		if (CLASS_BLACKLIST.contains(receiver))
			throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getName(), method));
		
		if (CLASS_WHITELIST.contains(receiver))
			return super.onStaticCall(invoker, receiver, method, args);
		
		throw new SecurityException(String.format("%s.%s method call not allowed.", receiver.getName(), method));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
		if (CLASS_BLACKLIST.contains(receiver))
			throw new SecurityException(String.format("%s constructor call not allowed.", receiver.getName()));
		
		if (CLASS_WHITELIST.contains(receiver))
			return super.onNewInstance(invoker, receiver, args);
		
		throw new SecurityException(String.format("%s constructor call not allowed.", receiver.getName()));
	}
}