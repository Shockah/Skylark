package scommands;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import shocky3.Shocky;

public interface ICommand {
	public void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args);
}