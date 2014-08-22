package sconsole;

public class ConsolePlugin extends Plugin {
	public final ConsoleThread thread;
	
	public ConsolePlugin(ConsoleThread thread) {
		super(thread.plugin.pinfo);
		this.thread = thread;
	}
	
	protected void onConsoleEnabled() {}
	protected void onConsoleDisabled() {}
}