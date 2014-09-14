package sconsole.channel;

import org.pircbotx.Channel;
import org.pircbotx.User;
import sconsole.ConsoleViewSplitter.Side;
import sconsole.IConsoleViewSelectList;

public class CSSLUser implements IConsoleViewSelectList, Comparable<CSSLUser> {
	public final Channel channel;
	public final User user;
	
	public final boolean isOp, isVoiced;
	
	public CSSLUser(Channel channel, User user) {
		this.channel = channel;
		this.user = user;
		
		isOp = user.getChannelsOpIn().contains(channel);
		isVoiced = user.getChannelsVoiceIn().contains(channel);
	}
	
	public void updateTabView(Side side, boolean active) {
		
	}

	public String caption() {
		char prefix = 0;
		if (isOp) prefix = '@';
		else if (isVoiced) prefix = '+';
		String text = user.getNick();
		if (prefix != 0) text = "" + prefix + text;
		return text;
	}

	public int compareTo(CSSLUser o) {
		if (isOp && o.isOp) return user.getNick().compareToIgnoreCase(o.user.getNick());
		else if (isOp != o.isOp) return isOp ? -1 : 1;
		if (isVoiced && o.isVoiced) return user.getNick().compareToIgnoreCase(o.user.getNick());
		else if (isVoiced != o.isVoiced) return isVoiced ? -1 : 1;
		return user.getNick().compareToIgnoreCase(o.user.getNick());
	}
}