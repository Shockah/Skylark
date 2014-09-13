package sconsole.channel;

import java.util.Collections;
import org.pircbotx.Channel;
import org.pircbotx.User;
import sconsole.ConsoleThread;
import sconsole.ConsoleViewSelectList;
import sconsole.ConsoleViewSplitter;
import com.googlecode.lanterna.input.Key;

public class ConsoleViewChannelUserList extends ConsoleViewSelectList<CSSLUser> {
	public final Channel channel;
	public ConsoleViewChannelInput view;
	public boolean markUpdate = true;
	
	public ConsoleViewChannelUserList(ConsoleThread thread, Channel channel) {
		super(thread);
		this.channel = channel;
	}

	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		switch (key.getKind()) {
			case Tab:
				if (view != null) {
					rect.thread.replaceFocus(view);
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
