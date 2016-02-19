package me.shockah.skylark.func;

@FunctionalInterface
public interface Action2<T1, T2> {
	public void call(T1 t1, T2 t2);
}