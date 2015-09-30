package pl.shockah;

import java.util.Arrays;
import pl.shockah.func.Action1;
import pl.shockah.func.Func;

public class Pool<T> {
	public static final int DEFAULT_POOL = 32;
	
	protected final Func<T> fCreate;
	protected final Action1<T> fReset;
	protected Object[] pool;
	protected boolean[] taken;
	
	public Pool(Func<T> fCreate) {
		this(fCreate, null, DEFAULT_POOL);
	}
	public Pool(Func<T> fCreate, int pool) {
		this(fCreate, null, pool);
	}
	public Pool(Func<T> fCreate, Action1<T> fReset) {
		this(fCreate, fReset, DEFAULT_POOL);
	}
	public Pool(Func<T> fCreate, Action1<T> fReset, int pool) {
		this.fCreate = fCreate;
		this.fReset = fReset;
		this.pool = new Object[pool];
		this.taken = new boolean[pool];
	}
	
	@SuppressWarnings("unchecked")
	public synchronized T obtain() {
		for (int i = 0; i < taken.length; i++)
			if (!taken[i]) {
				T obj = (T)pool[i];
				if (obj == null)
					obj = construct(i);
				taken[i] = true;
				if (fReset != null)
					fReset.f(obj);
				return obj;
			}
		int index = taken.length;
		T obj = construct(index);
		taken[index] = true;
		if (fReset != null)
			fReset.f(obj);
		return obj;
	}
	
	public synchronized void free(T obj) {
		for (int i = 0; i < taken.length; i++)
			if (taken[i] && pool[i] == obj) {
				taken[i] = false;
				return;
			}
	}
	
	protected synchronized T construct(int index) {
		T obj = fCreate.f();
		if (index >= taken.length) {
			pool = Arrays.copyOf(pool, pool.length * 4);
			taken = Arrays.copyOf(taken, taken.length * 4);
		}
		return obj;
	}
}