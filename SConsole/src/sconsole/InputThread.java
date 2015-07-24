package sconsole;

import com.googlecode.lanterna.input.Key;

public class InputThread extends Thread {
	public final Console console;
	protected boolean running = true;
	
	public InputThread(Console console) {
		this.console = console;
	}
	
	public void run() {
		try {
			while (running) {
				Key key;
				while ((key = console.screen.readInput()) != null) {
					if (console.preInputHandler.handle(key))
						continue;
					if (console.inputHandler != null)
						if (console.inputHandler.handle(key))
							continue;
					console.postInputHandler.handle(key);
				}
				sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}
	}
}