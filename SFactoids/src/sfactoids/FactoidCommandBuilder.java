package sfactoids;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.json.JSONObject;
import scommands.ICommand;
import shocky3.Shocky;

public class FactoidCommandBuilder {
	protected List<FactoidParser> list = Collections.synchronizedList(new LinkedList<FactoidParser>());
	
	public void add(FactoidParser... fps) {
		for (FactoidParser fp : fps) {
			if (!list.contains(fp)) {
				list.add(fp);
			}
		}
	}
	public void remove(FactoidParser... fps) {
		for (FactoidParser fp : fps) {
			list.remove(fp);
		}
	}
	public FactoidParser findByID(String id) {
		for (FactoidParser fp : list) {
			if (fp.id.equals(id)) {
				return fp;
			}
		}
		return null;
	}
	
	public ICommand build(JSONObject j, Shocky botApp, MessageEvent<PircBotX> e, String trigger, String message) {
		//String original = message;
		String originalCode = j.getString("code");
		String code = originalCode;
		while (true) {
			System.out.println(code);
			Matcher m = FactoidParser.REGEX_PARSER.matcher(code);
			if (m.find()) {
				FactoidParser fp = findByID(m.group(1));
				if (fp == null) {
					break;
				} else {
					code = fp.parse(j, botApp, e, trigger, m.group(2), message);
				}
			} else {
				break;
			}
		}
		final String fcode = code;
		return new ICommand(){
			public void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args) {
				e.respond(fcode);
			}
		};
	}
}