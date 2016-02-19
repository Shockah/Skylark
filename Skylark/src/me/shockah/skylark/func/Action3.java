package me.shockah.skylark.func;

@FunctionalInterface
public interface Action3<T1, T2, T3> {
	public void call(T1 t1, T2 t2, T3 t3);
}