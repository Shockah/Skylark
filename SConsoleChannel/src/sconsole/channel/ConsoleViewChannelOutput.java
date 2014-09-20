package sconsole.channel;

import sconsole.ConsoleThread;
import sconsole.ConsoleViewSplitter;
import sconsole.ConsoleViewTextareaIRCColors;
import com.googlecode.lanterna.input.Key;

public class ConsoleViewChannelOutput extends ConsoleViewTextareaIRCColors {
	public ConsoleViewSet set;
	
	public ConsoleViewChannelOutput(ConsoleThread thread) {
		super(thread);
	}
	
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		switch (key.getKind()) {
			case Tab:
				if (set != null) {
					rect.thread.replaceFocus(set.input);
				}
				break;
			default:
				super.handleInput(side, key);
				break;
		}
	}
}