package sconsole;

public class ConsoleViewTab extends ConsoleView {
	public ConsoleViewTabs view = null;
	
	public ConsoleViewTab(ConsoleThread thread) {
		super(thread);
	}
	
	public void update(ConsoleViewSplitter.Side side) {
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