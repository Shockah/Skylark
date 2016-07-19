package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.NamedCommand;
import io.shockah.skylark.factoids.db.Factoid;

public abstract class AbstractFactoidCommand<T, R> extends NamedCommand<T, R> {
	public final Factoid factoid;
	
	public AbstractFactoidCommand(Factoid factoid) {
		super(factoid.name);
		this.factoid = factoid;
	}
}