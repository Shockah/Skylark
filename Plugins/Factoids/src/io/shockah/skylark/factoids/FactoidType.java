package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.factoids.db.Factoid;

public abstract class FactoidType {
	public final String type;
	
	public FactoidType(String type) {
		this.type = type;
	}
	
	public abstract <T, R> NamedCommand<T, R> createCommand(Factoid factoid);
}