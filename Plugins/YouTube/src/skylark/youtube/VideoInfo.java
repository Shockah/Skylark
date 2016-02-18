package skylark.youtube;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.pircbotx.Colors;
import pl.shockah.json.JSONObject;
import skylark.old.util.TimeDuration;

public final class VideoInfo {
	public final String id;
	public final JSONObject json;
	public String uploader, title, description;
	public int duration, voteUp, voteDown, views;
	
	public VideoInfo(String id, JSONObject json) {
		this.id = id;
		this.json = json;
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
			
			sb.append(String.format("\n&b%s&r", title));
			sb.append(String.format("\nby &b%s&r", uploader));
			if (duration != 0)
				sb.append(String.format("\n%s long", TimeDuration.formatSeconds(duration)));
			if (views != 0)
				sb.append(
					String.format("\n%s view%s",
					formatter.format(views),
					views == 1 ? "" : "s")
				);
			if (voteUp + voteDown != 0) {
				DecimalFormatSymbols symbols2 = DecimalFormatSymbols.getInstance();
				symbols2.setDecimalSeparator('.');
				DecimalFormat formatter2 = new DecimalFormat("###.##", symbols2);
				
				sb.append(
					String.format("\n+%s / -%s (%s%%)",
					formatter.format(voteUp),
					formatter.format(voteDown),
					formatter2.format(100d * voteUp / (voteUp + voteDown)))
				);
			}
			
			if (includeShortUrl)
				sb.append(String.format("\nhttp://youtu.be/%s", id));
			
			String ret = sb.toString().substring(3);
			ret = ret.replace("&b", Colors.BOLD);
			ret = ret.replace("&r", Colors.NORMAL);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}