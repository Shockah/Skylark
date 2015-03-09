package scommands;

import shocky3.Plugin;

public abstract class TextCommand extends Command<String, String> {
	public TextCommand(Plugin plugin, String main, String... alts) {
		super(String.class, String.class, plugin, main, alts);
	}
}