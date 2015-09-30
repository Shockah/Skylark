package pl.shockah;

public final class Strings {
	public static String implode(Object[] objs, String separator) {
		return implode(objs,separator,0,objs.length-1);
	}
	public static String implode(Object[] objs, String separator, int start) {
		return implode(objs,separator,start,objs.length-1);
	}
	public static String implode(Object[] objs, String separator, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i <= end; i++) {
			if (i != start) sb.append(separator);
			sb.append(objs[i]);
		}
		return sb.toString();
	}
	
	public static boolean tryParseByte(String s, Box<Byte> box) {
		try {
			box.value = Byte.parseByte(s);
			return true;
		} catch (Exception e) { return false; }
	}
	public static boolean tryParseShort(String s, Box<Short> box) {
		try {
			box.value = Short.parseShort(s);
			return true;
		} catch (Exception e) { return false; }
	}
	public static boolean tryParseInt(String s, Box<Integer> box) {
		try {
			box.value = Integer.parseInt(s);
			return true;
		} catch (Exception e) { return false; }
	}
	public static boolean tryParseLong(String s, Box<Long> box) {
		try {
			box.value = Long.parseLong(s);
			return true;
		} catch (Exception e) { return false; }
	}
	public static boolean tryParseFloat(String s, Box<Float> box) {
		try {
			box.value = Float.parseFloat(s);
			return true;
		} catch (Exception e) { return false; }
	}
	public static boolean tryParseDouble(String s, Box<Double> box) {
		try {
			box.value = Double.parseDouble(s);
			return true;
		} catch (Exception e) { return false; }
	}
	
	private Strings() {}
}