package scommands;

import shocky3.Plugin;

public abstract class BinaryCommand extends Command<byte[], byte[]> {
	public BinaryCommand(Plugin plugin, String main, String... alts) {
		super(byte[].class, byte[].class, plugin, main, alts);
	}
}