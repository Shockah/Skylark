package sconsole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.Util;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class ConsoleThread extends Thread {
	public final Plugin plugin;
	
	public Screen screen = null;
	public ScreenRect rect = null;
	public TerminalPosition cursor = new TerminalPosition(-1, -1);
	public Borders borders = null;
	public ConsoleView view = null;
	public ConsoleViewTabs viewTabs = null;
	public ConsoleViewTab viewTab = null;
	public List<ConsoleView> focusStack = new LinkedList<>();
	
	public ConsoleThread(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		screen = TerminalFacade.createScreen();
		screen.startScreen();
		if (screen.getTerminal() instanceof SwingTerminal) {
			((SwingTerminal)screen.getTerminal()).getJFrame().addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e) {
					plugin.botApp.stop();
				}
			});
		}
		rect = new ScreenRect(this);
		borders = new Borders(rect);
		
		ConsoleViewSplitter cvs1 = new ConsoleViewSplitter(this);
		//ConsoleViewSplitter cvs2 = new ConsoleViewSplitter(this);
		ConsoleViewTabs cvtt = new ConsoleViewTabs(this);
		ConsoleViewTab cvt = new ConsoleViewTab(this);
		ConsoleViewOutput cvo = new ConsoleViewOutput(this);
		
		cvo.apply();
		
		view = cvs1;
		cvs1.setMain(cvtt, ConsoleViewSplitter.Side.Top);
		cvs1.setOff(cvt);
		
		cvtt.tabs.add(new ConsoleTab("Output", cvo));
		
		viewTab = cvtt.view = cvt;
		viewTabs = cvt.view = cvtt;
		
		ScreenWriter sw = new ScreenWriter(screen);
		synchronized (plugin.listeners) {for (IConsolePluginListener listener : plugin.listeners) {
			listener.onConsoleEnabled();
		}}
		while (plugin.botApp.running && plugin.running) {
			if (screen.resizePending()) {
				screen.completeRefresh();
				rect.setSizeToScreen();
				borders = new Borders(rect);
			}
			
			sw.setBackgroundColor(Color.BLACK);
			sw.setForegroundColor(Color.WHITE);
			sw.fillScreen(' ');
			
			if (view == null) {
				setFocus(null);
			} else {
				if (focusStack.isEmpty()) {
					if (view.focusable()) {
						setFocus(view);
					}
				} else {
					while (!focusStack.isEmpty() && !focus().focusable()) {
						popFocus();
					}
				}
				view.rect.x = 0;
				view.rect.y = 0;
				view.rect.w = rect.w;
				view.rect.h = rect.h;
				view.update(null);
				view.draw(null);
			}
			
			borders.drawAndClear(rect);
			if (cursor.getColumn() != -1 && cursor.getRow() != -1) {
				screen.getTerminal().setCursorVisible(true);
				screen.setCursorPosition(cursor);
			}
			screen.refresh();
			if (cursor.getColumn() == -1 || cursor.getRow() == -1) {
				screen.getTerminal().setCursorVisible(false);
			} else {
				screen.getTerminal().setCursorVisible(true);
				cursor.setColumn(-1);
				cursor.setRow(-1);
			}
			Util.sleep(50);
		}
		synchronized (plugin.listeners) {for (IConsolePluginListener listener : plugin.listeners) {
			listener.onConsoleDisabled();
		}}
		
		cvo.restore();
		
		screen.stopScreen();
		plugin.stopped = true;
	}
	
	public ConsoleView focus() {
		return focusStack.isEmpty() ? null : focusStack.get(focusStack.size() - 1);
	}
	public void setFocus(ConsoleView view) {
		ConsoleView focus = focus();
		if (focus != null) {
			if (view == focus) return;
			focus.onLoseFocus();
		}
		
		if (view == null) {
			focusStack.clear();
		} else {
			if (focusStack.contains(view)) {
				while (focus() != view) {
					focusStack.remove(focusStack.size() - 1);
				}
			} else {
				focusStack.add(view);
			}
			
			focus = focus();
			if (view != focus) {
				focusStack.add(view);
			}
			view.onFocus();
		}
	}
	public void replaceFocus(ConsoleView view) {
		if (view == null) {
			popFocus();
		} else {
			if (focusStack.isEmpty()) {
				setFocus(view);
			} else {
				focus().onLoseFocus();
				focusStack.set(focusStack.size() - 1, view);
				view.onFocus();
			}
		}
	}
	public void popFocus() {
		if (!focusStack.isEmpty()) {
			ConsoleView focus = focus();
			focus.onLoseFocus();
			focus = null;
			while (!focusStack.isEmpty()) {
				focus = focusStack.remove(focusStack.size() - 1);
				if (focus.focusable()) break;
				focus = null;
			}
			if (focus != null) {
				focus.onFocus();
			}
		}
	}
}