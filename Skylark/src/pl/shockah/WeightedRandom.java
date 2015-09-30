package pl.shockah;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class WeightedRandom<T> {
	protected LinkedList<Pair<T, Double>> list = new LinkedList<>();
	protected final Random rand;
	
	public WeightedRandom() {
		this(new Random());
	}
	public WeightedRandom(long seed) {
		this();
		rand.setSeed(seed);
	}
	public WeightedRandom(Random rand) {
		this.rand = rand;
	}
	
	public void add(T element, double weight) {
		list.add(new Pair<>(element, weight));
	}
	public void remove(T element) {
		Iterator<Pair<T, Double>> iterator = list.iterator();
		while (iterator.hasNext())
			if (iterator.next().a.equals(element)) iterator.remove();
	}
	
	public T get() {
		double r = rand.nextDouble();
		double t = 0d;
		for (Pair<T, Double> pair : list)
			t += pair.b;
		r *= t;
		
		for (Pair<T,Double> pair : list)
			if (r > pair.b)
				r -= pair.b;
			else
				return pair.a;
		
		return null;
	}
}