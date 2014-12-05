package sfactoids;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import pl.shockah.json.JSONObject;
import scommands.CommandResult;
import scommands.ICommand;
import shocky3.pircbotx.event.GenericUserMessageEvent;

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
	
	public ICommand build(JSONObject j, GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
		//String original = message;
		String originalCode = j.getString("code");
		String code = originalCode;
		while (true) {
			String oldCode = code;
			Matcher m = FactoidParser.REGEX_PARSER.matcher(code);
			if (m.find()) {
				FactoidParser fp = findByID(m.group(1));
				code = m.group(2);
				if (fp == null) {
					break;
				} else {
					switch (fp.resultType()) {
						case FactoidParser.TYPE_STRING_CODE:
							code = fp.parseStringCode(j, e, trigger, args, code, result);
							break;
						case FactoidParser.TYPE_ICOMMAND:
							return fp.parseICommand(j, e, trigger, args, code, result);
					}
				}
			} else {
				break;
			}
			if (code == null || code.equals("") || oldCode.equals(code)) {
				break;
			}
		}
		final String fcode = code;
		return new ICommand(){
			public void call(GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
				result.add(fcode);
			}
		};
	}
}