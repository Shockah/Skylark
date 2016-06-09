package io.shockah.skylark.func;

@FunctionalInterface
public interface Func1<T1, R> {
	public R call(T1 t1);
}