package skylark.commands;

import java.util.Arrays;
import pl.shockah.json.JSONList;
import skylark.JSONThing;
import skylark.pircbotx.event.GenericUserMessageEvent;

public class SplitCommandInputParser extends CommandInputParser {
	public JSONThing parse(GenericUserMessageEvent e, String arg) {
		JSONList<String> list = new JSONList<>();
		list.addAll(Arrays.asList(arg.split("\\s")));
		return new JSONThing(list);
	}
}