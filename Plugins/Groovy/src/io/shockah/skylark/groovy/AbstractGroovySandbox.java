package io.shockah.skylark.groovy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;

public abstract class AbstractGroovySandbox extends GroovyInterceptor {
	public abstract boolean isInstanceMethodCallAllowed(Object obj, String method, Object... args);
	
	public abstract boolean isClassMethodCallAllowed(Class<?> clazz, String method, Object... args);
	
	public abstract boolean isConstructorAllowed(Class<?> clazz, Object... args);
	
	public abstract boolean isInstanceFieldGetAllowed(Object obj, String field);
	
	public abstract boolean isInstanceFieldSetAllowed(Object obj, String field, Object value);
	
	public abstract boolean isClassFieldGetAllowed(Class<?> clazz, String field);
	
	public abstract boolean isClassFieldSetAllowed(Class<?> clazz, String field, Object value);
	
	@Override
	public Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
		if (isInstanceMethodCallAllowed(receiver, method, args))
			return super.onMethodCall(invoker, receiver, method, args);
		else
			throw new SecurityException(String.format("%s.%s instance method call not allowed.", receiver.getClass().getName(), method));
	}
	
	@Override
	public Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
		if (isClassMethodCallAllowed(receiver, method, args))
			return super.onStaticCall(invoker, receiver, method, args);
		else
			throw new SecurityException(String.format("%s.%s static method call not allowed.", receiver.getName(), method));
	}
	
	@Override
	public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
		if (isConstructorAllowed(receiver, args))
			return super.onNewInstance(invoker, receiver, args);
		else
			throw new SecurityException(String.format("%s.%s constructor call not allowed.", receiver.getName()));
	}
	
	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
		if (receiver instanceof Class<?>) {
			Class<?> clazz = (Class<?>)receiver;
			try {
				clazz.getField(property);
				
				if (isClassFieldGetAllowed(clazz, property))
					return super.onGetProperty(invoker, receiver, property);
				else
					throw new SecurityException(String.format("%s.%s static field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e) {
			}
			
			String method = String.format("get%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
			try {
				Method classMethod = clazz.getClass().getMethod(method);
				if (!Modifier.isStatic(classMethod.getModifiers())) {
					clazz = clazz.getClass();
					if (isInstanceMethodCallAllowed(clazz, method))
						return super.onGetProperty(invoker, receiver, property);
					else
						throw new SecurityException(String.format("%s.%s instance method call not allowed.", clazz.getName(), method));
				}
			} catch (NoSuchMethodException e) {
			}
			if (isClassMethodCallAllowed(clazz, method))
				return super.onGetProperty(invoker, receiver, property);
			else
				throw new SecurityException(String.format("%s.%s static method call not allowed.", clazz.getName(), method));
		} else {
			Class<?> clazz = receiver.getClass();
			try {
				clazz.getField(property);
				
				if (isInstanceFieldGetAllowed(receiver, property))
					return super.onGetProperty(invoker, receiver, property);
				else
					throw new SecurityException(String.format("%s.%s instance field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e) {
			}
			
			String method = String.format("get%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
			if (isInstanceMethodCallAllowed(receiver, method))
				return super.onGetProperty(invoker, receiver, property);
			else
				throw new SecurityException(String.format("%s.%s instance method call not allowed.", clazz.getName(), method));
		}
	}
	
	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
		if (receiver instanceof Class<?>) {
			Class<?> clazz = (Class<?>)receiver;
			try {
				clazz.getField(property);
				
				if (isClassFieldSetAllowed(clazz, property, value))
					return super.onSetProperty(invoker, receiver, property, value);
				else
					throw new SecurityException(String.format("%s.%s static field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e) {
			}
			
			String method = String.format("set%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
			try {
				Method classMethod = clazz.getClass().getMethod(method);
				if (!Modifier.isStatic(classMethod.getModifiers())) {
					clazz = clazz.getClass();
					if (isInstanceMethodCallAllowed(clazz, method, value))
						return super.onSetProperty(invoker, receiver, property, value);
					else
						throw new SecurityException(String.format("%s.%s instance method call not allowed.", clazz.getName(), method));
				}
			} catch (NoSuchMethodException e) {
			}
			if (isClassMethodCallAllowed(clazz, method, value))
				return super.onSetProperty(invoker, receiver, property, value);
			else
				throw new SecurityException(String.format("%s.%s static method call not allowed.", clazz.getName(), method));
		} else {
			Class<?> clazz = receiver.getClass();
			try {
				clazz.getField(property);
				
				if (isInstanceFieldSetAllowed(receiver, property, value))
					return super.onSetProperty(invoker, receiver, property, value);
				else
					throw new SecurityException(String.format("%s.%s instance field access not allowed.", clazz.getName(), property));
			} catch (NoSuchFieldException e) {
			}
			
			String method = String.format("set%s%s", property.substring(0, 1).toUpperCase(), property.substring(1));
			if (isInstanceMethodCallAllowed(receiver, method, value))
				return super.onSetProperty(invoker, receiver, property, value);
			else
				throw new SecurityException(String.format("%s.%s instance method call not allowed.", clazz.getName(), method));
		}
	}
}