package sconsole;

import pl.shockah.Math2;
import com.googlecode.lanterna.screen.Screen;

public class Clip {
	public final Screen screen;
	public Clip parent = null;
	public int x = 0, y = 0, w, h;
	
	public Clip(Screen screen) {
		this.screen = screen;
		w = screen.getTerminalSize().getColumns();
		h = screen.getTerminalSize().getRows();
	}
	public Clip(Clip clip) {
		this(clip.screen);
		parent = clip;
		w = clip.w;
		h = clip.h;
	}
	public Clip(Clip clip, int x, int y, int w, int h) {
		this(clip);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public Clip(Screen screen, int x, int y, int w, int h) {
		this(screen);
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public int X() {
		int x = this.x;
		if (parent != null)
			x += parent.X();
		return x;
	}
	public int Y() {
		int y = this.y;
		if (parent != null)
			y += parent.Y();
		return y;
	}
	
	public void fix() {
		if (parent == null)
			return;
		x = Math2.limit(x, 0, parent.w - 1);
		y = Math2.limit(y, 0, parent.h - 1);
		w = Math2.limit(w, 0, parent.w - x);
		h = Math2.limit(h, 0, parent.h - y);
	}
}