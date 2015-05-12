package sconsole;

import java.util.ArrayList;
import java.util.Collections;
import pl.shockah.SelectList;

public class ListView extends ScrollView {
	public static class Item extends View {
		public Item(Console console, Clip clip) {
			super(console, clip);
		}
		
		protected final void update(View parent, SplitterView.Side side) {
			throw new UnsupportedOperationException();
		}
		
		protected void update(View parent, SplitterView.Side side, boolean selected) {
			
		}
	}
	
	public SelectList<Item> items = new SelectList<>(Collections.synchronizedList(new ArrayList<Item>()));
	
	public ListView(Console console, Clip clip) {
		super(console, clip);
	}
	
	public void recalcMinSize(SplitterView.Side side) {
		int maxWidth = 0;
		int totalHeight = 0;
		for (Item item : items) {
			item.clip = new Clip(clip);
			item.recalcMinSize(side);
			totalHeight += item.clip.h;
			maxWidth = Math.max(item.clip.w, maxWidth);
		}
		contentW = maxWidth;
		clip.h = totalHeight;
	}
	
	protected void updateContent(View parent, SplitterView.Side side, Clip clip) {
		synchronized (items) {
			//TODO: only update visible items;
			int yy = 0;
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				item.clip.y = yy;
				item.update(this, side, items.getCurrentIndex() == i);
				yy += item.clip.h;
			}
		}
	}
}