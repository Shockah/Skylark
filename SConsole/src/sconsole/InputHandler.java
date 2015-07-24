package sconsole;

import com.googlecode.lanterna.input.Key;

public interface InputHandler {
	public boolean handle(Key key);
}