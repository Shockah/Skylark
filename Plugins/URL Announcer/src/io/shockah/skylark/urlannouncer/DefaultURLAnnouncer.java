package io.shockah.skylark.urlannouncer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.kevinsawicki.http.HttpRequest;

public class DefaultURLAnnouncer extends URLAnnouncer {
	public static final Pattern TITLE_PATTERN = Pattern.compile("\\<title\\>(.*?)\\</title\\>", Pattern.CASE_INSENSITIVE);
	
	@Override
	public String getTitleForURL(String url) {
		try {
			HttpRequest req = HttpRequest.get(url).accept("text/html").followRedirects(true);
			String[] splitContentType = req.contentType().split(";");
			for (int i = 0; i < splitContentType.length; i++) splitContentType[i] = splitContentType[i].trim();
			if (splitContentType[0].equals("text/html")) {
				Matcher m = TITLE_PATTERN.matcher(req.body());
				if (m.find())
					return String.format("[%s]", m.group(1).replaceAll("\\s+", " ").trim());
			}
		} catch (Exception e) {
		}
		return null;
	}
}