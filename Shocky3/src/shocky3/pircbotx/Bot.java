package shocky3.pircbotx;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class Bot extends PircBotX {
	public Bot(Configuration<? extends Bot> configuration) {
		super(configuration);
	}
	
	protected void sendRawLineToServer(String line) {
		System.out.println(">>> " + line + " <<<");
		super.sendRawLineToServer(line);
	}
}