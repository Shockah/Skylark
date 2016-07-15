package io.shockah.skylark.groovy;

import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.factoids.FactoidType;
import io.shockah.skylark.factoids.db.Factoid;

public class GroovyFactoidType extends FactoidType {
	public static final String TYPE = "groovy";
	
	public final GroovyPlugin plugin;
	
	public GroovyFactoidType(GroovyPlugin plugin) {
		super(TYPE);
		this.plugin = plugin;
	}

	@Override
	public <T, R> NamedCommand<T, R> createCommand(Factoid factoid) {
		return new GroovyFactoidCommand<T, R>(plugin, factoid);
	}
}