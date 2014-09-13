package sconsole;

public interface IConsoleViewSelectList {
	public void updateTabView(ConsoleViewSplitter.Side side, boolean active);
	public String caption();
}