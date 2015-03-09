package shocky3;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class MultilineString {
	public static <T> String with(Collection<T> els, Function<T, String> f) {
		MultilineString str = new MultilineString();
		str.add(els, f);
		return str.toString();
	}
	public static <T> String with(Collection<String> lines) {
		MultilineString str = new MultilineString();
		str.add(lines);
		return str.toString();
	}
	public static <T> String with(String[] lines) {
		MultilineString str = new MultilineString();
		str.add(lines);
		return str.toString();
	}
	
	protected StringBuilder sb = new StringBuilder();
	
	public <T> void add(Collection<T> els, Function<T, String> f) {
		for (T el : els) {
			String line = f.apply(el);
			if (line == null || line.length() == 0)
				continue;
			
			if (sb.length() != 0)
				sb.append("\n");
			sb.append(line);
		}
	}
	public void add(Collection<String> lines) {
		add(lines, line -> line);
	}
	public void add(String[] lines) {
		add(Arrays.asList(lines));
	}
	
	public String toString() {
		return sb.toString();
	}
}