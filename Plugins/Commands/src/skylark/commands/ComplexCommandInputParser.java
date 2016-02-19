package skylark.commands;

import java.util.ArrayList;
import java.util.List;
import me.shockah.skylark.event.GenericUserMessageEvent;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.Pair;
import pl.shockah.StrBuffer;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;

public class ComplexCommandInputParser extends CommandInputParser {
	public static JSONObject parseComplexArgs(String arg) {
		JSONObject json = new JSONObject();
		
		String actualArg = null;
		
		Pair<String[], String[]> tokenResult = parseTokens(arg);
		String[] tokens = tokenResult.a;
		String[] separators = tokenResult.b;
		
		boolean[] isOption = new boolean[tokens.length];
		boolean[] isFlag = new boolean[tokens.length];
		
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length() >= 2 && tokens[i].charAt(0) == '-')
				isFlag[i] = !(isOption[i] = tokens[i].length() >= 3 && tokens[i].charAt(tokens[i].length() - 1) == ':');
		}
		
		for (int i = 0; i < tokens.length; i++) {
			if (isOption[i]) {
				if (i + 1 < tokens.length) {
					json.put(tokens[i].substring(1, tokens[i].length() - 2), tokens[i + 1]);
					i++;
				}
			} else if (isFlag[i]) {
				if (!json.contains("flags"))
					json.put("flags", new JSONList<String>());
				JSONList<String> jFlags = json.getList("flags").ofStrings();
				jFlags.add(tokens[i].substring(1));
			} else {
				StringBuilder sb = new StringBuilder();
				for (int j = i; j < tokens.length; j++) {
					sb.append(tokens[j]);
					if (j < separators.length)
						sb.append(separators[j]);
				}
				actualArg = sb.toString();
				break;
			}
		}
		if (!StringUtils.isBlank(actualArg))
			json.put("arg", actualArg);
		
		return json;
	}
	
	public static Pair<String[], String[]> parseTokens(String arg) {
		List<String> tokens = new ArrayList<>();
		List<String> separators = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder();
		StrBuffer strb = new StrBuffer();
		strb.append(arg);
		strb.setPos(0);
		
		char lastc = 0;
		while (!strb.atEnd()) {
			char c = strb.readChar();
			if (Character.isWhitespace(c)) {
				if (lastc == '\\') {
					sb.append(c);
				} else {
					tokens.add(sb.toString());
					sb = new StringBuilder();
					separators.add("" + c);
				}
			} else {
				if (c == '\\') {
					if (lastc == '\\') {
						sb.append('\\');
						c = 0;
					} else {
						sb.append("\\" + c);
					}
				} else {
					sb.append(c);
				}
			}
			lastc = c;
		}
		
		return new Pair<>(tokens.toArray(new String[0]), separators.toArray(new String[0]));
	}
	
	public JSONObject parse(GenericUserMessageEvent e, String arg) {
		return parseComplexArgs(arg);
	}
}