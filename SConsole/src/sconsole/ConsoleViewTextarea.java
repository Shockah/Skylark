package sconsole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsoleViewTextarea extends ConsoleView {
	public int scrollback;
	public List<String> lines = Collections.synchronizedList(new ArrayList<String>());
	protected final List<String> cachedList = new ArrayList<>();
	
	public ConsoleViewTextarea(ConsoleThread thread) {
		this(thread, 500);
	}
	public ConsoleViewTextarea(ConsoleThread thread, int scrollback) {
		super(thread);
		this.scrollback = scrollback;
	}
	
	public void draw(ConsoleViewSplitter.Side side) {
		int yy = rect.h - 1;
		synchronized (lines) {
			while (lines.size() > scrollback) lines.remove(0);
			for (int i = lines.size() - 1; i >= 0; i--) {
				List<String> wrapped = linewrap(lines.get(i), "  ");
				for (int j = wrapped.size() - 1; j >= 0; j--) {
					rect.draw(0, yy--, wrapped.get(j));
					if (yy < 0) break;
				}
				if (yy < 0) break;
			}
		}
	}
	
	public List<String> linewrap(String line, String indent) {
		cachedList.clear();
		String[] spl = line.split("((?<=\\s)|(?=\\s))");
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < spl.length; i++) {
			String s = spl[i];
			int len = sb.length();
			int len2 = s.length();
			if (len + len2 <= rect.w || len == 0) {
				sb.append(s);
			} else {
				cachedList.add(sb.toString());
				sb = new StringBuilder(indent);
				if (!s.matches("\\s")) {
					sb.append(s);
				}
			}
		}
		if (sb.length() != 0) {
			cachedList.add(sb.toString());
		}
		return cachedList;
	}
}