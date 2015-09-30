package skylark.urlannouncer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.kevinsawicki.http.HttpRequest;

public class DefaultURLAnnouncer extends URLAnnouncer {
	public static final Pattern
		TITLE_PATTERN = Pattern.compile("\\<title\\>(.*?)\\</title\\>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	
	public DefaultURLAnnouncer(skylark.Plugin plugin) {
		super(plugin, PRIORITY_LOW);
	}
	
	public boolean matches(String url) {
		return true;
	}
	
	public String text(String url) {
		return retrieveTitle(url);
	}
	
	public String retrieveTitle(String url) {
		try {
			HttpRequest req = HttpRequest.get(url).accept("text/html").followRedirects(true);
			//TODO: handle HTTP 301 redirects
			if (req.ok()) {
				String[] splitContentType = req.contentType().split(";");
				for (int i = 0; i < splitContentType.length; i++)
					splitContentType[i] = splitContentType[i].trim();
				if (splitContentType[0].equals("text/html")) {
					Matcher m = TITLE_PATTERN.matcher(req.body());
					if (m.find())
						return m.group(1).replaceAll("\\s+", " ").trim();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}