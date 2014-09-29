package sconsole;

import com.googlecode.lanterna.input.Key;

public abstract class ConsoleViewLongTextfield extends ConsoleView {
	public StringBuilder sb = new StringBuilder();
	
	public ConsoleViewLongTextfield(ConsoleThread thread) {
		super(thread);
	}
	
	public void update(ConsoleViewSplitter.Side side) {
		super.update(side);
		if (side != null && side.h) {
			throw new IllegalStateException();
		}
		rect.h = 1;
	}
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		switch (key.getKind()) {
			case NormalKey:
				sb.append(key.getCharacter());
				break;
			case Backspace:
				if (sb.length() == 0) break;
				sb.deleteCharAt(sb.length() - 1);
				break;
			case Enter:
				if (sb.length() == 0) break;
				handleOutput(sb.toString());
				sb = new StringBuilder();
				break;
			default:
				super.handleInput(side, key);
				break;
		}
	}
	
	public abstract void handleOutput(String message);
	
	public void draw(ConsoleViewSplitter.Side side) {
		rect.draw(0, 0, sb.toString());
		if (rect.thread.focus() == this) {
			rect.setCursor(sb.length(), 0);
		}
	}
}