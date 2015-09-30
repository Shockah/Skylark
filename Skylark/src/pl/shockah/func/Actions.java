package pl.shockah.func;

import java.util.Collection;

@SuppressWarnings("unused") public final class Actions {
	public static <T> void forEach(Collection<T> cl, Action action) {
		for (T t : cl)
			action.f();
	}
	public static <T> void forEach(Collection<T> cl, Action1<T> action) {
		for (T t : cl)
			action.f(t);
	}
	public static <T, A> void forEach(Collection<T> cl, Action1<A> action, A a) {
		for (T t : cl)
			action.f(a);
	}
	public static <T, A> void forEach(Collection<T> cl, Action2<T, A> action, A a) {
		for (T t : cl)
			action.f(t, a);
	}
	public static <T, A, B> void forEach(Collection<T> cl, Action2<A, B> action, A a, B b) {
		for (T t : cl)
			action.f(a, b);
	}
	public static <T, A, B> void forEach(Collection<T> cl, Action3<T, A, B> action, A a, B b) {
		for (T t : cl)
			action.f(t, a, b);
	}
	public static <T, A, B, C> void forEach(Collection<T> cl, Action3<A, B, C> action, A a, B b, C c) {
		for (T t : cl)
			action.f(a, b, c);
	}
	
	private Actions() { }
}