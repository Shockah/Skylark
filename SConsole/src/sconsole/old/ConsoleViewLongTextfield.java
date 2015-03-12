package sconsole.old;

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
		if (rect.w == 0) return;
		rect.h = (int)Math.ceil(1f * (sb.length() + 1) / rect.w);
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
		String msg = sb.toString();
		for (int yy = 0; yy < rect.h; yy++) {
			rect.draw(0, yy, msg.substring(yy * rect.w, Math.min((yy + 1) * rect.w, msg.length())));
		}
		if (rect.thread.focus() == this) {
			int xx = msg.length() % rect.w;
			rect.setCursor(xx, rect.h - 1);
		}
	}
}