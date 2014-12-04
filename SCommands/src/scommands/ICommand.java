package scommands;

import shocky3.pircbotx.event.GenericUserMessageEvent;

public interface ICommand {
	public String call(GenericUserMessageEvent e, String trigger, String args, boolean chain);
}