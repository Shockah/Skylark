package sconsole;

public abstract class View {
	public final Console console;
	public Clip clip;
	public int minSize = 0;
	
	public View(Console console, Clip clip) {
		this.console = console;
		this.clip = clip;
	}
	
	public final void requestUpdate() {
		console.updateRequested.add(this);
		console.update();
	}
	public final void clear() {
		
	}
	
	public void recalcMinSize(SplitterView.Side side) {
		minSize = 0;
	}
	protected abstract void update(SplitterView.Side side);
}