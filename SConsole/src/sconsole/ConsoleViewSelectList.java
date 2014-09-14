package sconsole;

import java.util.Collections;
import java.util.LinkedList;
import pl.shockah.SelectList;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal.Color;

public abstract class ConsoleViewSelectList<T extends IConsoleViewSelectList> extends ConsoleView {
	public int scroll = 0, totalSize = 0, lastL = 0;
	public SelectList<T> tabs = new SelectList<>(Collections.synchronizedList(new LinkedList<T>()));
	
	public ConsoleViewSelectList(ConsoleThread thread) {
		super(thread);
	}
	
	protected int maxScroll(ConsoleViewSplitter.Side side) {
		if (side == null) return 0;
		if (side.h) {
			return Math.max(0, totalSize - rect.h);
		} else {
			return Math.max(0, totalSize - rect.w);
		}
	}
	protected void fixScroll(ConsoleViewSplitter.Side side) {
		if (side == null) return;
		scroll = Math.min(Math.max(scroll, 0), maxScroll(side));
	}
	protected void fixScroll(ConsoleViewSplitter.Side side, int pos, int w, int lastL) {
		if (side == null) return;
		
		int ex1 = pos;
		int ex2 = pos + w;
		int limit1 = ex1;
		int limit2 = ex2 - lastL;
		scroll = Math.min(Math.max(scroll, Math.min(limit1, limit2)), Math.max(limit1, limit2));
		
		//fixScroll(side);
	}
	
	public void update(ConsoleViewSplitter.Side side) {
		super.update(side);
		if (side == null) return;
		
		synchronized (tabs) {
			preUpdate(side);
			if (side.h) {
				int w = 2;
				totalSize = 0;
				int pos = 0;
				for (T tab : tabs) {
					boolean sel = tabs.getCurrent() == tab;
					if (sel) fixScroll(side, pos, 1, lastL);
					tab.updateTabView(side, sel);
					int ww = tab.caption().length() + 2;
					if (ww > w) w = ww;
					totalSize++;
					pos++;
				}
				rect.w = w;
				if (totalSize > lastL) rect.w++;
			} else {
				int w = 0;
				totalSize = 0;
				for (T tab : tabs) {
					boolean sel = tabs.getCurrent() == tab;
					if (sel) fixScroll(side, w, tab.caption().length() + 2, lastL);
					tab.updateTabView(side, sel);
					w += tab.caption().length() + 2;
					totalSize += tab.caption().length() + 2;
				}
				rect.w = w;
				rect.h = 1;
				if (totalSize > lastL) rect.h++;
			}
		}
	}
	public void handleInput(ConsoleViewSplitter.Side side, Key key) {
		super.handleInput(side, key);
		if (side == null) return;
		
		synchronized (tabs) {
			if (side.h) {
				if (key.getKind() == Kind.ArrowUp) {
					tabs.previous();
				} else if (key.getKind() == Kind.ArrowDown) {
					tabs.next();
				}
			} else {
				if (key.getKind() == Kind.ArrowLeft) {
					tabs.previous();
				} else if (key.getKind() == Kind.ArrowRight) {
					tabs.next();
				}
			}
		}
		
		if (key.getKind() == Kind.Enter) {
			onAction(tabs.getCurrent());
		}
	}
	
	public void preUpdate(ConsoleViewSplitter.Side side) {}
	public void preDraw(ConsoleViewSplitter.Side side) {}
	public abstract void onAction(T tab);
	
	public void draw(ConsoleViewSplitter.Side side) {
		if (side == null) return;
		lastL = side.h ? rect.h : rect.w;
		boolean focus = rect.thread.focus() == this;
		boolean hasBar = false;
		
		synchronized (tabs) {
			preDraw(side);
			if (side.h) {
				int ww = rect.w - 2;
				if (totalSize > lastL) {
					hasBar = true;
					ww--;
				}
				for (int i = 0; i < tabs.size(); i++) {
					T tab = tabs.get(i);
					boolean sel = tabs.getCurrentIndex() == i;
					
					StringBuilder sb = new StringBuilder();
					sb.append(tab.caption());
					while (sb.length() < ww) sb.append(' ');
					
					String text = String.format(sel ? "<%s>" : " %s ", sb);
					rect.draw(0, i - scroll, text, focus && sel ? Color.BLACK : Color.WHITE, focus && sel ? Color.WHITE : Color.BLACK);
				}
			} else {
				int xx = 0;
				if (totalSize > lastL) {
					hasBar = true;
				}
				for (int i = 0; i < tabs.size(); i++) {
					T tab = tabs.get(i);
					boolean sel = tabs.getCurrentIndex() == i;
					String text = String.format(sel ? "<%s>" : " %s ", tab.caption());
					rect.draw(xx - scroll, 0, text, focus && sel ? Color.BLACK : Color.WHITE, focus && sel ? Color.WHITE : Color.BLACK);
					xx += text.length();
				}
			}
		}
		
		if (hasBar) {
			if (side.h) {
				float fSbarSize = Math.min(1f * rect.h / totalSize, 1f);
				int iSbarSize = Math.max((int)Math.floor(fSbarSize * rect.h), 1);
				float fSbarScroll = Math.min(1f * scroll / totalSize, 1f);
				int iSbarScroll = (int)Math.ceil(fSbarScroll * rect.h);
				
				for (int i = 0; i < rect.h; i++) {
					rect.draw(rect.w - 1, i, (i >= iSbarScroll && i < iSbarScroll + iSbarSize) ? Borders.charsBar[focus ? 4 : 3] : Borders.charsBar[1]);
				}
			} else {
				float fSbarSize = Math.min(1f * rect.w / totalSize, 1f);
				int iSbarSize = Math.max((int)Math.floor(fSbarSize * rect.w), 1);
				float fSbarScroll = Math.min(1f * scroll / totalSize, 1f);
				int iSbarScroll = (int)Math.ceil(fSbarScroll * rect.w);
				
				for (int i = 0; i < rect.w; i++) {
					rect.draw(i, rect.h - 1, (i >= iSbarScroll && i < iSbarScroll + iSbarSize) ? Borders.charsBar[focus ? 4 : 3] : Borders.charsBar[1]);
				}
			}
		}
	}
}