package skylark.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import pl.shockah.func.Action1;

public final class Reflection {
	private static final Action1<Exception>
		defaultExceptionFunc = e -> e.printStackTrace();
	
	public static void run(Action1<Reflection> func, Action1<Exception> exceptionFunc) {
		func.f(new Reflection(exceptionFunc));
	}
	
	public static void run(Action1<Reflection> func) {
		run(func, defaultExceptionFunc);
	}
	
	private final Action1<Exception> exceptionFunc;
	
	private Reflection(Action1<Exception> exceptionFunc) {
		this.exceptionFunc = exceptionFunc;
	}
	
	public void setFinal(Class<?> cls, String fieldName, boolean state) {
		try {
			setFinal(cls.getDeclaredField(fieldName), state);
		} catch (Exception e) {
			if (exceptionFunc != null)
				exceptionFunc.f(e);
		}
	}
	
	public void setFinal(Field field, boolean state) {
		try {
			field.setAccessible(true);
			
			int modifiers = field.getModifiers();
			modifiers = state ? (modifiers | Modifier.FINAL) : (modifiers & ~Modifier.FINAL);
			
			Field mfield = Field.class.getDeclaredField("modifiers");
			mfield.setAccessible(true);
			mfield.setInt(field, modifiers);
		} catch (Exception e) {
			if (exceptionFunc != null)
				exceptionFunc.f(e);
		}
	}
	
	public <T> void setFinalFieldValue(Class<T> cls, String fieldName, T instance, Object value) {
		try {
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			boolean wasFinal = (field.getModifiers() & Modifier.FINAL) != 0;
			
			if (wasFinal)
				setFinal(field, false);
			field.set(instance, value);
			if (wasFinal)
				setFinal(field, true);
		} catch (Exception e) {
			if (exceptionFunc != null)
				exceptionFunc.f(e);
		}
	}
	
	public void setFinalFieldValue(Field field, Object instance, Object value) {
		try {
			field.setAccessible(true);
			boolean wasFinal = (field.getModifiers() & Modifier.FINAL) != 0;
			
			if (wasFinal)
				setFinal(field, false);
			field.set(instance, value);
			if (wasFinal)
				setFinal(field, true);
		} catch (Exception e) {
			if (exceptionFunc != null)
				exceptionFunc.f(e);
		}
	}
}