package pl.shockah.func;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class Funcs {
	@SuppressWarnings("unused")
	public static <T, R> List<R> forEach(Collection<T> cl, Func<R> func) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f());
		return list;
	}
	
	public static <T, R> List<R> forEach(Collection<T> cl, Func1<T, R> func) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f(t));
		return list;
	}
	@SuppressWarnings("unused")
	public static <T, R, A> List<R> forEach(Collection<T> cl, Func1<A, R> func, A a) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f(a));
		return list;
	}
	
	public static <T, R, A> List<R> forEach(Collection<T> cl, Func2<T, A, R> func, A a) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f(t, a));
		return list;
	}
	@SuppressWarnings("unused")
	public static <T, R, A, B> List<R> forEach(Collection<T> cl, Func2<A, B, R> func, A a, B b) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f(a, b));
		return list;
	}
	
	public static <T, R, A, B> List<R> forEach(Collection<T> cl, Func3<T, A, B, R> func, A a, B b) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f(t, a, b));
		return list;
	}
	@SuppressWarnings("unused")
	public static <T, R, A, B, C> List<R> forEach(Collection<T> cl, Func3<A, B, C, R> func, A a, B b, C c) {
		List<R> list = new LinkedList<>();
		for (T t : cl)
			list.add(func.f(a, b, c));
		return list;
	}
	
	private Funcs() { }
}