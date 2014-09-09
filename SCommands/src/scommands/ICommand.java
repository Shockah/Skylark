package scommands;

import shocky3.Bot;
import shocky3.Shocky;
import shocky3.pircbotx.GenericUserMessageEvent;

public interface ICommand {
	public void call(Shocky botApp, GenericUserMessageEvent<Bot> e, String trigger, String args);
}