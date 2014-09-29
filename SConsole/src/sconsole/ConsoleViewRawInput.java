package sconsole;

import java.util.List;
import shocky3.BotManager;
import shocky3.pircbotx.Bot;
import com.googlecode.lanterna.input.Key;

public class ConsoleViewRawInput extends ConsoleViewLongTextfield {
	public ConsoleViewRawSet set;
	
	public ConsoleViewRawInput(ConsoleThread thread) {
		super(thread);
	}
	
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		switch (key.getKind()) {
			case Tab:
				if (set != null) {
					rect.thread.replaceFocus(set.output);
				}
				break;
			default:
				super.handleInput(side, key);
				break;
		}
	}
	
	public void handleOutput(String message) {
		List<BotManager> managers = rect.thread.plugin.botApp.serverManager.botManagers;
		synchronized (managers) {
			if (managers.isEmpty()) return;
			List<Bot> bots = managers.get(0).bots;
			synchronized (bots) {
				if (bots.isEmpty()) return;
				bots.get(0).sendRaw().rawLine(message);
			}
		}
	}
}