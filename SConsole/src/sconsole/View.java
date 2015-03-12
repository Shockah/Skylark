package sconsole;

public abstract class View {
	public final Console console;
	public Clip clip;
	
	public View(Console console, Clip clip) {
		this.console = console;
		this.clip = clip;
	}
	
	public final void requestUpdate() {
		console.updateRequested.add(this);
		console.update();
	}
	
	protected abstract void update();
}