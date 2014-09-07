package scommands;

import org.pircbotx.PircBotX;
import shocky3.Shocky;
import shocky3.pircbotx.NullableChannelUserEvent;

public interface ICommand {
	public void call(Shocky botApp, NullableChannelUserEvent<PircBotX> e, String trigger, String args);
}