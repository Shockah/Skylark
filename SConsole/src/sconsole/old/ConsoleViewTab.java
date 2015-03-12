package sconsole.old;

public class ConsoleViewTab extends ConsoleView {
	public ConsoleViewTabs view = null;
	
	public ConsoleViewTab(ConsoleThread thread) {
		super(thread);
	}
	
	public boolean focusable() {
		if (view == null) return false;
		ConsoleTab tab = view.tabs.getCurrent();
		if (tab != null && tab.view != null && tab.view.focusable()) return true;
		return false;
	}
	public void onFocus() {
		if (view == null) {
			rect.thread.popFocus();
			return;
		}
		ConsoleTab tab = view.tabs.getCurrent();
		if (tab != null && tab.view != null && tab.view.focusable()) {
			rect.thread.replaceFocus(tab.view);
			return;
		}
		rect.thread.popFocus();
	}
	
	public void update(ConsoleViewSplitter.Side side) {
		super.update(side);
		if (view == null) return;
		ConsoleTab tab = view.tabs.getCurrent();
		if (tab == null || tab.view == null) return;
		tab.view.rect.x = rect.x;
		tab.view.rect.y = rect.y;
		tab.view.rect.w = rect.w;
		tab.view.rect.h = rect.h;
		tab.view.update(side);
		rect.x = tab.view.rect.x;
		rect.y = tab.view.rect.y;
		rect.w = tab.view.rect.w;
		rect.h = tab.view.rect.h;
	}
	
	public void draw(ConsoleViewSplitter.Side side) {
		if (view == null) return;
		ConsoleTab tab = view.tabs.getCurrent();
		if (tab == null || tab.view == null) return;
		tab.view.draw(side);
	}
}