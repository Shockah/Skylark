package sconsole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class Console {
	public static final char[] chars = "   ═ ╝╚╩ ╗╔╦║╣╠╬".toCharArray();
	public static final char[] charsBar = " ░▒▓█".toCharArray();
	
	public static char borderWithSides(boolean l, boolean r, boolean u, boolean d) {
		return chars[(l ? 1 : 0) + (r ? 2 : 0) + (u ? 4 : 0) + (d ? 8 : 0)];
	}
	
	public final Plugin plugin;
	public Screen screen = null;
	public Clip clip = null;
	public boolean[] borders = null;
	
	protected View view = null;
	protected final List<View> updateRequested = Collections.synchronizedList(new LinkedList<View>());
	protected View focus = null;
	protected List<View> focusStack = Collections.synchronizedList(new LinkedList<View>());
	
	public Console(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void start() {
		screen = TerminalFacade.createScreen();
		screen.startScreen();
		if (screen.getTerminal() instanceof SwingTerminal) {
			((SwingTerminal)screen.getTerminal()).getJFrame().addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e) {
					plugin.botApp.stop();
				}
			});
		}
		clip = new Clip(screen);
		
		screen.getTerminal().addResizeListener(size -> onResize());
		onResize();
	}
	
	public void stop() {
		view = null;
		updateRequested.clear();
		clip = null;
		screen.stopScreen();
		screen = null;
	}
	
	protected void onResize() {
		borders = new boolean[clip.w * clip.h];
		updateAll();
	}
	
	public void updateAll() {
		synchronized (updateRequested) {
			updateRequested.clear();
			updateRequested.add(view);
			update();
		}
	}
	
	public void update() {
		synchronized (updateRequested) {
			while (!updateRequested.isEmpty()) {
				View view = updateRequested.remove(0);
				popFocus();
				view.clear();
				view.update(null, null);
				view.putBorder();
			}
			screen.refresh();
		}
	}
	
	public void pushFocus(View view) {
		synchronized (focusStack) {
			focusStack.add(view);
			focus = view;
		}
	}
	public void popFocus() {
		synchronized (focusStack) {
			if (!focusStack.isEmpty())
				focusStack.remove(focusStack.size() - 1);
			if (focusStack.isEmpty())
				pushFocus(view);
			else
				focus = focusStack.get(focusStack.size() - 1);
		}
	}
	
	public void clearBorder() {
		Arrays.fill(borders, false);
	}
	
	public boolean hasBorderAt(int x, int y) {
		if (x < 0 || y < 0 || x >= clip.w || y >= clip.h)
			return true;
		return borders[x + y * clip.w];
	}
	public void putBorder(int x, int y) {
		putBorder(x, y, Color.DEFAULT, Color.DEFAULT);
	}
	public void putBorder(int x, int y, Color fg, Color bg) {
		if (x < 0 || y < 0 || x >= clip.w || y >= clip.h)
			return;
		borders[x + y * clip.w] = true;
		screen.putString(x, y, "" + borderWithSides(hasBorderAt(x - 1, y), hasBorderAt(x + 1, y), hasBorderAt(x, y - 1), hasBorderAt(x, y + 1)), fg, bg);
	}
}