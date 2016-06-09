package io.shockah.skylark.func;

@FunctionalInterface
public interface Func2<T1, T2, R> {
	public R call(T1 t1, T2 t2);
}