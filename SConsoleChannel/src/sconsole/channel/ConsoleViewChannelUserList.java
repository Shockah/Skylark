package sconsole.channel;

import java.util.Collections;
import org.pircbotx.Channel;
import org.pircbotx.User;
import sconsole.old.ConsoleThread;
import sconsole.old.ConsoleViewSelectList;
import sconsole.old.ConsoleViewSplitter;
import com.googlecode.lanterna.input.Key;

public class ConsoleViewChannelUserList extends ConsoleViewSelectList<CSSLUser> {
	public final Channel channel;
	public ConsoleViewSet set;
	public boolean markUpdate = true;
	
	public ConsoleViewChannelUserList(ConsoleThread thread, Channel channel) {
		super(thread);
		this.channel = channel;
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

	public void preUpdate(ConsoleViewSplitter.Side side) {
		if (markUpdate) {
			markUpdate = false;
			tabs.clear();
			for (User user : channel.getUsers()) {
				tabs.add(new CSSLUser(channel, user));
			}
			Collections.sort(tabs);
		}
	}
	public void onAction(CSSLUser tab) {}
}
