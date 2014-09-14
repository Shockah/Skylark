package sphp;

import java.util.Collection;
import org.pircbotx.User;
import pl.shockah.func.Func1;
import pl.shockah.json.JSONObject;
import pl.shockah.json.JSONParser;
import shocky3.Shocky;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;
import com.github.kevinsawicki.http.HttpRequest;

public class PHP {
	public final Shocky botApp;
	public final Plugin plugin;
	
	public PHP(Plugin plugin) {
		this.botApp = plugin.botApp;
		this.plugin = plugin;
	}
	
	public String parse(GenericUserMessageEvent<Bot> e, String trigger, String args, String code) {
		String url = botApp.settings.getStringForChannel(null, plugin, "url");
		
		StringBuilder sb = build(e, new StringBuilder(), args);
		sb.append(code);
		
		try {
			HttpRequest req = HttpRequest.post(url).form("code", sb.toString());
			if (req.ok()) {
				JSONObject j = new JSONParser().parseObject(req.body());
				if (j.contains("error")) {
					JSONObject jError = j.getObject("error");
					return jError.getString("message");
				}
				if (j.contains("safe_errors")) {
					String safeErrors = j.getString("safe_errors");
					if (safeErrors != null) return safeErrors;
				}
				return j.getString("output");
			}
		} catch (Exception ex) {ex.printStackTrace();}
		return code;
	}
	
	protected StringBuilder build(GenericUserMessageEvent<Bot> e, StringBuilder sb, String args) {
		varStringSimple(sb, "bot", e.getBot().getNick());
		varStringSimple(sb, "sender", e.getUser().getNick());
		varStringSimple(sb, "channel", e.getChannel().getName());
		varStringSimple(sb, "input", args);
		varStringSimple(sb, "ioru", args.equals("") ? e.getUser().getNick() : args);
		varStringSimpleArray(sb, "users", e.getChannel().getUsers(), new Func1<User, String>(){ public String f(User user) { return user.getNick(); } });
		return sb;
	}
	
	protected void varStringSimple(StringBuilder sb, String variable, String value) {
		sb.append(String.format("$%s = %s;", variable, formatStringSimple(value)));
	}
	protected String formatStringSimple(String value) {
		if (value == null) {
			return "null";
		} else {
			return String.format("'%s'", value.replace("\\", "\\\\").replace("'", "\\'"));
		}
	}
	
	protected <T> void varStringSimpleArray(StringBuilder sb, String variable, Collection<T> value) {
		varStringSimpleArray(sb, variable, value, new Func1<T, String>(){ public String f(T a) { return a == null ? "null" : a.toString(); } });
	}
	protected <T> void varStringSimpleArray(StringBuilder sb, String variable, Collection<T> value, Func1<T, String> f) {
		sb.append(String.format("$%s = %s;", variable, formatStringSimpleArray(value, f)));
	}
	protected <T> String formatStringSimpleArray(Collection<T> value, Func1<T, String> f) {
		StringBuilder sb = new StringBuilder();
		for (T a : value) {
			sb.append(",");
			sb.append(formatStringSimple(f.f(a)));
		}
		return String.format("array(%s)", sb.toString().substring(1));
	}
}