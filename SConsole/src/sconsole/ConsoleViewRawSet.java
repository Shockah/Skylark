package sconsole;

import sconsole.ConsoleTab;

public class ConsoleViewRawSet {
	public final ConsoleTab tab;
	public final ConsoleViewRawInput input;
	public final ConsoleViewRawOutput output;
	
	public ConsoleViewRawSet(ConsoleTab tab, ConsoleViewRawInput input, ConsoleViewRawOutput output) {
		this.tab = tab;
		this.input = input;
		this.output = output;
	}
}