package io.shockah.skylark.youtube;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.pircbotx.Colors;
import io.shockah.skylark.util.TimeDuration;

public final class Video {
	public String id;
	public String title;
	public String channelTitle;
	public int durationInSeconds;
	public long views;
	public int likes;
	public int dislikes;
	
	public String getShortURL() {
		return String.format("http://youtu.be/%s", id);
	}
	
	public String format() {
		return format(true);
	}
	
	public String format(boolean includeShortUrl) {
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		symbols.setGroupingSeparator(',');
		DecimalFormat formatter = new DecimalFormat("###,###", symbols);
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format(" | &b%s&r", title));
		sb.append(String.format(" | by &b%s&r", channelTitle));
		if (durationInSeconds != 0)
			sb.append(String.format(" | %s long", TimeDuration.formatSeconds(durationInSeconds)));
		if (views != 0)
			sb.append(String.format(" | %s view%s", formatter.format(views), views == 1 ? "" : "s"));
		if (likes + dislikes != 0) {
			DecimalFormatSymbols symbols2 = DecimalFormatSymbols.getInstance();
			symbols2.setDecimalSeparator('.');
			DecimalFormat formatter2 = new DecimalFormat("###.##", symbols2);
			sb.append(String.format(
				" | +%s / -%s (%s%%)",
				formatter.format(likes),
				formatter.format(dislikes),
				formatter2.format(100d * likes / (likes + dislikes))
			));
		}
		if (includeShortUrl)
			sb.append(String.format(" | %s", getShortURL()));
		
		String ret = sb.toString().substring(3);
		ret = ret.replace("&b", Colors.BOLD);
		ret = ret.replace("&r", Colors.NORMAL);
		return ret;
	}
}