package sconsole;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;

public class ConsoleView {
	protected final ScreenRect rect;
	
	public ConsoleView(ConsoleThread thread) {
		rect = new ScreenRect(thread, 0, 0, 0, 0);
	}
	
	public boolean focusable() {return true;}
	public void onFocus() {}
	public void onLoseFocus() {}
	
	public void update(ConsoleViewSplitter.Side side) {
		if (rect.thread.focus() == this) {
			Key key;
			while ((key = rect.screen().readInput()) != null) {
				handleInput(side, key);
			}
		}
	}
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		if (key.getKind() == Kind.Escape) {
			rect.thread.popFocus();
		}
	}
	public void draw(ConsoleViewSplitter.Side side) {
		
	}
}