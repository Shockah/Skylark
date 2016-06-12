package io.shockah.skylark.util;

import java.nio.charset.Charset;

public final class StringUtils {
	public static String trimToByteLength(String str, int byteLength) {
		return trimToByteLength(str, byteLength, Charset.defaultCharset());
	}
	
	public static String trimToByteLength(String str, int byteLength, Charset charset) {
		if (byteLength < 0)
			throw new IllegalArgumentException();
		if (byteLength == 0)
			return "";
		
		while (true) {
			byte[] bytes = str.getBytes(charset);
			if (bytes.length <= byteLength)
				return str;
			
			int trim = Math.max((byteLength - bytes.length) / 2, 1); //each char can be either 1 or 2 bytes
			str = str.substring(0, str.length() - trim);
		}
	}
	
	private StringUtils() {
		throw new UnsupportedOperationException();
	}
}