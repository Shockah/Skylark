package sconsole.channel;

import org.pircbotx.Channel;
import sconsole.ConsoleThread;
import sconsole.ConsoleViewLongTextfield;
import sconsole.ConsoleViewSplitter;
import com.googlecode.lanterna.input.Key;

public class ConsoleViewChannelInput extends ConsoleViewLongTextfield {
	public final Channel channel;
	public ConsoleViewSet set;
	
	public ConsoleViewChannelInput(ConsoleThread thread, Channel channel) {
		super(thread);
		this.channel = channel;
	}
	
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		switch (key.getKind()) {
			case Tab:
				if (set != null) {
					rect.thread.replaceFocus(set.userlist);
				}
				break;
			default:
				super.handleInput(side, key);
				break;
		}
	}
	
	public void handleOutput(String message) {
		String[] spl = message.split("\\s");
		if (spl[0].equalsIgnoreCase("/me") && spl.length >= 2) {
			message = message.substring(spl[0].length() + 1);
			channel.send().action(message);
		} else {
			channel.send().message(message);
		}
	}
}