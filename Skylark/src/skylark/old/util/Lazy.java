package skylark.old.util;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import pl.shockah.func.Func;

public class Lazy<T> extends LazyInitializer<T> {
	public static <R> Lazy<R> of(Func<R> func) {
		return new Lazy<R>(func);
	}
	
	protected final Func<T> func;
	
	public Lazy(Func<T> func) {
		this.func = func;
	}
	
	protected T initialize() throws ConcurrentException {
		return func.f();
	}
	
	public synchronized T get() {
		T obj = null;
		try {
			obj = super.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}