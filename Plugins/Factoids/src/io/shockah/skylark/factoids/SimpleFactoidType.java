package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.factoids.db.Factoid;

public class SimpleFactoidType extends FactoidType {
	public static final String TYPE = "simple";
	
	public SimpleFactoidType() {
		super(TYPE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, R> NamedCommand<T, R> createCommand(Factoid factoid) {
		return (NamedCommand<T, R>)new SimpleFactoidCommand(factoid);
	}
}