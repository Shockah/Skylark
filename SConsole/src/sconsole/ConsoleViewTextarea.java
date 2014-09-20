package sconsole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.shockah.Box;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;

public class ConsoleViewTextarea extends ConsoleView {
	public String indent = "  ";
	public int scrollback, scroll = 0;
	protected int totalSize = 0;
	protected final List<TextareaLine> lines = Collections.synchronizedList(new ArrayList<TextareaLine>());
	protected final TerminalSize cachedSize = new TerminalSize(0, 0);
	protected final List<String> cachedList = new ArrayList<>();
	
	public ConsoleViewTextarea(ConsoleThread thread) {
		this(thread, 500);
	}
	public ConsoleViewTextarea(ConsoleThread thread, int scrollback) {
		super(thread);
		this.scrollback = scrollback;
	}
	
	public void add(String... lines) {
		synchronized (lines) {for (String line : lines) {
			this.lines.add(new TextareaLine(line, indent));
		}}
	}
	public void clear() {
		lines.clear();
		totalSize = 0;
		scroll = 0;
	}
	
	protected void reinitLines() {
		synchronized (lines) {for (TextareaLine line : lines) {
			line.reinit(indent);
		}}
	}
	
	protected int maxScroll() {
		return Math.max(0, totalSize - rect.h);
	}
	protected void fixScroll() {
		scroll = Math.min(Math.max(scroll, 0), maxScroll());
	}
	
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		switch (key.getKind()) {
			case ArrowUp:
				scroll++;
				fixScroll();
				break;
			case ArrowDown:
				scroll--;
				fixScroll();
				break;
			case PageUp:
				scroll += rect.h;
				fixScroll();
				break;
			case PageDown:
				scroll -= rect.h;
				fixScroll();
				break;
			default:
				super.handleInput(side, key);
				break;
		}
	}
	
	public void draw(ConsoleViewSplitter.Side side) {
		if (cachedSize.getColumns() <= 0 || cachedSize.getRows() <= 0) {
			cachedSize.setColumns(Math.max(rect.w, 0));
			cachedSize.setRows(Math.max(rect.h, 0));
		} else if (cachedSize.getColumns() != rect.w || cachedSize.getRows() != rect.h) {
			cachedSize.setColumns(Math.max(rect.w, 0));
			cachedSize.setRows(Math.max(rect.h, 0));
			reinitLines();
		}
		
		synchronized (lines) {
			while (lines.size() > scrollback) {
				totalSize -= lines.remove(0).wrapped.size();
			}
			fixScroll();
			int yy = rect.h - 1 + scroll;
			
			for (int i = lines.size() - 1; i >= 0; i--) {
				TextareaLine line = lines.get(i);
				if (line.wrapped.isEmpty()) {
					line.reinit(indent);
				}
				Box<Color> colorBackground = new Box<>(Color.BLACK), colorForeground = new Box<>(Color.WHITE);
				for (int j = line.wrapped.size() - 1; j >= 0; j--) {
					drawLine(0, yy--, line.wrapped.get(j), colorBackground, colorForeground);
					if (yy < 0) break;
				}
				if (yy < 0) break;
			}
		}
		
		float fSbarSize = Math.min(1f * rect.h / totalSize, 1f);
		int iSbarSize = Math.max((int)Math.floor(fSbarSize * rect.h), 1);
		float fSbarScroll = Math.min(1f * scroll / totalSize, 1f);
		int iSbarScroll = (int)Math.ceil(fSbarScroll * rect.h);
		
		boolean focus = rect.thread.focus() == this;
		for (int i = 0; i < rect.h; i++) {
			rect.draw(rect.w - 1, i, ((rect.h - i) > iSbarScroll && (rect.h - i) <= iSbarScroll + iSbarSize) ? Borders.charsBar[focus ? 4 : 3] : Borders.charsBar[1]);
		}
	}
	public void drawLine(int x, int y, String line, Box<Color> colorBackground, Box<Color> colorForeground) {
		rect.draw(x, y, line, colorForeground.value, colorBackground.value);
	}
	
	public List<String> linewrap(String line, String indent) {
		cachedList.clear();
		if (rect.w <= 0 || rect.h <= 0) {
			return cachedList;
		}
		String[] spl = line.split("((?<=\\s)|(?=\\s))");
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < spl.length; i++) {
			String s = spl[i];
			int len = length(sb);
			int len2 = length(s);
			if (len + len2 <= rect.w - 1 || len == 0) {
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
	public int length(CharSequence s) {
		return s.length();
	}
	
	public class TextareaLine {
		public final String message;
		public final List<String> wrapped = new ArrayList<>();
		
		public TextareaLine(String message, String indent) {
			this.message = message;
			reinit(indent);
		}
		
		public void reinit(String indent) {
			totalSize -= wrapped.size();
			wrapped.clear();
			wrapped.addAll(linewrap(message, indent));
			totalSize += wrapped.size();
		}
	}
}