package scommands;

import shocky3.pircbotx.Bot;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public interface ICommand {
	public void call(GenericUserMessageEvent<Bot> e, String trigger, String args);
}