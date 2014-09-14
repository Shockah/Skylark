package sconsole.channel;

import sconsole.ConsoleTab;

public class ConsoleViewSet {
	public final ConsoleTab tab;
	public final ConsoleViewChannelUserList userlist;
	public final ConsoleViewChannelInput input;
	public final ConsoleViewChannelOutput output;
	
	public ConsoleViewSet(ConsoleTab tab, ConsoleViewChannelUserList userlist, ConsoleViewChannelInput input, ConsoleViewChannelOutput output) {
		this.tab = tab;
		this.userlist = userlist;
		this.input = input;
		this.output = output;
	}
}