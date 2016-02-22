package me.shockah.skylark.ident;

public abstract class IdentMethodFactory {
	public final String name;
	public final String prefix;
	
	public IdentMethodFactory(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}
	
	public abstract IdentMethod create(IdentService service);
}