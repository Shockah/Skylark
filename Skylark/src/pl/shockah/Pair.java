package pl.shockah;

public class Pair<A,B> {
	public A a;
	public B b;

	public Pair(A first, B second) {
		super();
		this.a = first;
		this.b = second;
	}

	public boolean equals(Object instance) {
		if (instance instanceof Pair<?, ?>) {
			Pair<?, ?> other = (Pair<?, ?>)instance;
			boolean check1 = false, check2 = false;
			
			if (a != null)
				check1 = a.equals(other.a);
			if (!check1 && other.a != null)
				check1 = other.a.equals(a);
			if (b != null)
				check2 = b.equals(other.b);
			if (!check2 && other.b != null)
				check2 = other.b.equals(b);
			
			return check1 && check2;
		}
		return false;
	}
	public int hashCode() {
		return a.hashCode() * b.hashCode();
	}
	public String toString() {
		return "(" + a + "|" + b + ")";
	}
	
	public Object[] getBoth() {
		return new Object[]{a, b};
	}
}