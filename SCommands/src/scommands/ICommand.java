package scommands;

import shocky3.pircbotx.event.GenericUserMessageEvent;

public interface ICommand {
	public void call(GenericUserMessageEvent e, String trigger, String args, CommandResult result);
}