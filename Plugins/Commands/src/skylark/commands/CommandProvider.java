package skylark.commands;

public abstract class CommandProvider {
	public final skylark.Plugin plugin;
	
	public CommandProvider(skylark.Plugin plugin) {
		this.plugin = plugin;
	}
}