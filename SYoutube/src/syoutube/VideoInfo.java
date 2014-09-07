package syoutube;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.pircbotx.Colors;
import shocky3.TimeDuration;

public final class VideoInfo {
	public final String id;
	public String uploader, title, description;
	public int duration, voteUp, voteDown, views;
	
	public VideoInfo(String id) {
		this.id = id;
	}
	
	public String format() {
		return format(true);
	}
	public String format(boolean includeShortUrl) {
		try {
			DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
			symbols.setGroupingSeparator(',');
			DecimalFormat formatter = new DecimalFormat("###,###", symbols);
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.format(" | &b%s&r", title));
			sb.append(String.format(" | by &b%s&r", uploader));
			if (duration != 0) {
				sb.append(String.format(" | %s long", TimeDuration.formatSeconds(duration)));
			}
			if (views != 0) {
				sb.append(String.format(" | %s view%s", formatter.format(views), views == 1 ? "" : "s"));
			}
			if (voteUp + voteDown != 0) {
				DecimalFormatSymbols symbols2 = DecimalFormatSymbols.getInstance();
				symbols2.setDecimalSeparator('.');
				DecimalFormat formatter2 = new DecimalFormat("###.##", symbols2);
				
				sb.append(String.format(" | +%s / -%s (%s%%)", formatter.format(voteUp), formatter.format(voteDown), formatter2.format(100d * voteUp / (voteUp + voteDown))));
			}
			
			if (includeShortUrl) {
				sb.append(String.format(" | http://youtu.be/%s", id));
			}
			
			String ret = sb.toString().substring(3);
			ret = ret.replace("&b", Colors.BOLD);
			ret = ret.replace("&r", Colors.NORMAL);
			return ret;
		} catch (Exception e) {e.printStackTrace();}
		return "";
	}
}