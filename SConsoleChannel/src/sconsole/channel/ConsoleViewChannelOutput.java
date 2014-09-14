package sconsole.channel;

import com.googlecode.lanterna.input.Key;
import sconsole.ConsoleThread;
import sconsole.ConsoleViewSplitter;
import sconsole.ConsoleViewTextarea;

public class ConsoleViewChannelOutput extends ConsoleViewTextarea {
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