package sconsole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class Console {
	public final Plugin plugin;
	public Screen screen = null;
	public Clip clip = null;
	
	protected View view = null;
	protected final List<View> updateRequested = Collections.synchronizedList(new LinkedList<View>());
	
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
		
		screen.getTerminal().addResizeListener(size -> updateAll());
		updateAll();
	}
	
	public void stop() {
		view = null;
		updateRequested.clear();
		clip = null;
		screen.stopScreen();
		screen = null;
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
				view.update(null);
			}
		}
	}
}