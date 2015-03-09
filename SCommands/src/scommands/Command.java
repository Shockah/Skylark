package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class Command<T, R> {
	public final Class<?> clsInput, clsReturn;
	
	public final shocky3.Plugin plugin;
	public final String main;
	public final List<String> alts = Collections.synchronizedList(new LinkedList<String>());
	
	public Command(Class<T> clsInput, Class<R> clsReturn, shocky3.Plugin plugin, String main, String... alts) {
		this.clsInput = clsInput;
		this.clsReturn = clsReturn;
		this.plugin = plugin;
		this.main = main.toLowerCase();
		for (String alt : alts)
			this.alts.add(alt.toLowerCase());
	}
	
	@SuppressWarnings("unchecked")
	protected final R callGeneric(GenericUserMessageEvent e, Object input, CommandStack stack) {
		return call(e, (T)input, stack);
	}
	public abstract R call(GenericUserMessageEvent e, T input, CommandStack stack);
}