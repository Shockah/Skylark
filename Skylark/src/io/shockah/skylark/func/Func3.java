package io.shockah.skylark.func;

@FunctionalInterface
public interface Func3<T1, T2, T3, R> {
	public R call(T1 t1, T2 t2, T3 t3);
}