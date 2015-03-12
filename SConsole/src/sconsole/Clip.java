package sconsole;

import com.googlecode.lanterna.screen.Screen;

public class Clip {
	public final Screen screen;
	public int x, y, w, h;
	
	public Clip(Screen screen) {
		this(screen, 0, 0, screen.getTerminalSize().getColumns(), screen.getTerminalSize().getRows());
	}
	public Clip(Clip clip) {
		this(clip, 0, 0, clip.w, clip.h);
	}
	public Clip(Clip clip, int x, int y, int w, int h) {
		this(clip.screen, clip.x + x, clip.y + y, w, h);
	}
	public Clip(Screen screen, int x, int y, int w, int h) {
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
}