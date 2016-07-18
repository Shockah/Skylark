package io.shockah.skylark.commands;

import java.util.List;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import io.shockah.skylark.event.GenericUserMessageEvent;

public final class CommandCall {
	public final GenericUserMessageEvent event;
	public final Medium inputMedium;
	public Medium outputMedium = null;
	
	public CommandCall(GenericUserMessageEvent event) {
		this.event = event;
		
		if (event.getEvent() instanceof MessageEvent)
			inputMedium = Medium.Channel;
		else if (event.getEvent() instanceof PrivateMessageEvent)
			inputMedium = Medium.Private;
		else if (event.getEvent() instanceof NoticeEvent)
			inputMedium = Medium.Notice;
		else
			throw new IllegalArgumentException();
	}
	
	public void respond(List<String> lines) {
		Medium medium = outputMedium;
		if (medium == null)
			medium = inputMedium;
		
		for (String line : lines) {
			switch (medium) {
				case Channel:
					event.getChannel().send().message(line);
					break;
				case Private:
					event.getUser().send().message(line);
					break;
				case Notice:
					event.getUser().send().notice(line);
					break;
			}
		}
	}
	
	public static enum Medium {
		Channel, Private, Notice;
	}
}