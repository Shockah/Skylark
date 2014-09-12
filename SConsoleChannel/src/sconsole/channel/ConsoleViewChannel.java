package sconsole.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sconsole.ConsoleThread;
import sconsole.ConsoleView;
import sconsole.ConsoleViewSplitter;

public class ConsoleViewChannel extends ConsoleView {
	public List<String> lines = Collections.synchronizedList(new ArrayList<String>());
	
	public ConsoleViewChannel(ConsoleThread thread) {
		super(thread);
	}
	
	public void draw(ConsoleViewSplitter.Side side) {
		int yy = rect.h - 1;
		synchronized (lines) {
			while (lines.size() > 500) lines.remove(0);
			for (int i = lines.size() - 1; i >= 0; i--) {
				rect.draw(0, yy--, lines.get(i));
				if (yy < 0) break;
			}
		}
	}
}