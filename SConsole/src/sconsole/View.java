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
		clip.clear();
	}
	
	public void recalcMinSize(SplitterView.Side side) {
		minSize = 0;
	}
	protected abstract void update(View parent, SplitterView.Side side);
	
	public void putBorder() {
		int x1 = clip.X();
		int y1 = clip.Y();
		int x2 = x1 + clip.w - 1;
		int y2 = y1 + clip.h - 1;
		
		for (int xx = x1 - 1; xx <= x2 + 1; xx++) {
			console.putBorder(xx, y1 - 1);
			console.putBorder(xx, y2 + 1);
		}
		for (int yy = y1; yy <= y2; yy++) {
			console.putBorder(x1 - 1, yy);
			console.putBorder(x2 + 1, yy);
		}
	}
	
	public void focus() {
		console.pushFocus(this);
	}
}