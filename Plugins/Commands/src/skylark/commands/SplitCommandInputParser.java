package skylark.commands;

import java.util.Arrays;
import pl.shockah.json.JSONList;
import pl.shockah.json.JSONObject;
import skylark.pircbotx.event.GenericUserMessageEvent;

public class SplitCommandInputParser extends CommandInputParser {
	public JSONObject parse(GenericUserMessageEvent e, String arg) {
		JSONList<String> list = new JSONList<>();
		list.addAll(Arrays.asList(arg.split("\\s")));
		return JSONObject.make("args", list);
	}
}