package surlannounce;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import pl.shockah.func.Func;
import shocky3.Plugin;
import shocky3.Shocky;
import com.github.kevinsawicki.http.HttpRequest;

public class DefaultURLAnnouncer extends URLAnnouncer {
	public static final Pattern
		REGEX_TITLE = Pattern.compile("\\<title\\>(.*?)\\</title\\>");
	
	public DefaultURLAnnouncer(Plugin plugin) {
		super(plugin);
	}

	public void provide(List<Pair<Func<String>, EPriority>> candidates, Shocky botApp, MessageEvent e, final String url) {
		candidates.add(new Pair<Func<String>, EPriority>(new Func<String>(){
			public String f() {
				String title = retrieveTitle(url);
				return title == null ? null : String.format("[%s]", title);
			}
		}, EPriority.Low));
	}
	
	public String retrieveTitle(String url) {
		try {
			HttpRequest req = HttpRequest.get(url).accept("text/html").followRedirects(true);
			String[] splitContentType = req.contentType().split(";");
			for (int i = 0; i < splitContentType.length; i++) splitContentType[i] = splitContentType[i].trim();
			if (splitContentType[0].equals("text/html")) {
				Matcher m = REGEX_TITLE.matcher(req.body());
				if (m.find()) {
					return m.group(1).replaceAll("\\s+", " ").trim();
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
}