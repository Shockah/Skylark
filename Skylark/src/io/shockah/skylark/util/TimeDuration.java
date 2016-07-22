package io.shockah.skylark.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeDuration {
	public static final Pattern TIME_DURATION_PATTERN = Pattern.compile("(\\d+)([smhdw])(\\s?\\d+[smhdw])*", Pattern.CASE_INSENSITIVE);
	public static final Pattern TIME_DURATION_TOKEN_PATTERN = Pattern.compile("(\\d+)([smhdw])", Pattern.CASE_INSENSITIVE);
	
	public static String format(Date date) {
		return format(date, new Date());
	}
	
	public static String format(Date date1, Date date2) {
		return formatMiliseconds(date2.getTime() - date1.getTime());
	}
	
	public static String formatSeconds(int s) {
		if (s <= 0)
			return "0s";
		
		int m = s / 60;
		s %= 60;
		
		int h = m / 60;
		m %= 60;
		
		int d = h / 24;
		h %= 24;
		
		int w = d / 7;
		d %= 7;
		
		StringBuilder sb = new StringBuilder();
		if (w != 0)
			sb.append(String.format(" %dw", w));
		if (w + d != 0)
			sb.append(String.format(" %dd", d));
		if (w + d + h != 0)
			sb.append(String.format(" %dh", h));
		if (w + d + h + m != 0)
			sb.append(String.format(" %dm", m));
		if (w + d + h + m + s != 0)
			sb.append(String.format(" %ds", s));
		return sb.toString().substring(1);
	}
	
	public static int parseSeconds(String formatted) {
		int seconds = 0;
		Matcher m = TIME_DURATION_TOKEN_PATTERN.matcher(formatted);
		while (m.find()) {
			int units = Integer.parseInt(m.group(1));
			String unitType = m.group(2);
			seconds += parseSeconds(units, unitType);
		}
		return seconds;
	}
	
	private static int parseSeconds(int units, String unitType) {
		switch (unitType.toLowerCase()) {
			case "s":
				return units;
			case "m":
				return units * 60;
			case "h":
				return units * 60 * 60;
			case "d":
				return units * 60 * 60 * 24;
			case "w":
				return units * 60 * 60 * 24 * 7;
		}
		throw new IllegalArgumentException();
	}
	
	public static String formatMiliseconds(long ms) {
		return formatSeconds((int)(ms / 1000l));
	}
	
	private TimeDuration() {
		throw new UnsupportedOperationException();
	}
}