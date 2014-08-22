package sconsole;

public class ConsoleView {
	protected final ScreenRect rect;
	
	public ConsoleView(ConsoleThread thread) {
		rect = new ScreenRect(thread, 0, 0, 0, 0);
	}
	
	public void update(ConsoleViewSplitter.Side side) {}
	public void draw(ConsoleViewSplitter.Side side) {}
}