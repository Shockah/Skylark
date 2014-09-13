package sconsole;

public class ConsoleViewTabs extends ConsoleViewSelectList<ConsoleTab> {
	public ConsoleViewTab view = null;
	
	public ConsoleViewTabs(ConsoleThread thread) {
		super(thread);
	}

	public void onAction(ConsoleTab tab) {
		rect.thread.setFocus(view);
	}
}