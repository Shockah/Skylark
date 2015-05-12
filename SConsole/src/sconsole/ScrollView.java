package sconsole;

import sconsole.old.Borders;

public abstract class ScrollView extends View {
	public int scrollH = 0, scrollV = 0;
	public int contentW = -1, contentH = -1;
	
	public ScrollView(Console console, Clip clip) {
		super(console, clip);
	}
	
	protected final Clip contentClip() {
		int w = clip.w;
		int h = clip.h;
		if (contentW != -1)
			h--;
		if (contentH != -1)
			w--;
		return new Clip(clip, 0, 0, w, h);
	}
	
	public int maxScrollV() {
		return Math.max(contentH == -1 ? 0 : (contentH - clip.h), 0);
	}
	public int maxScrollH() {
		return Math.max(contentW == -1 ? 0 : (contentW - clip.w), 0);
	}
	
	protected void update(View parent, SplitterView.Side side) {
		Clip clip = contentClip();
		updateContent(parent, side, new Clip(clip, -scrollH, -scrollV, contentW == -1 ? clip.w : contentW, contentH == -1 ? clip.h : contentH));
		
		if (contentH != -1) {
			float fSbarSize = Math.min(1f * clip.h / contentH, 1f);
			int iSbarSize = Math.max((int)Math.floor(fSbarSize * clip.h), 1);
			float fSbarScroll = Math.min(1f * scrollH / contentH, 1f);
			int iSbarScroll = (int)Math.ceil(fSbarScroll * clip.h);
			
			for (int i = 0; i < clip.h; i++)
				clip.put(clip.w - 1, i, ((clip.h - i) > iSbarScroll && (clip.h - i) <= iSbarScroll + iSbarSize) ? Borders.charsBar[/*focus ? */4/* : 3*/] : Borders.charsBar[1]);
		}
		if (contentW != -1) {
			float fSbarSize = Math.min(1f * clip.w / contentW, 1f);
			int iSbarSize = Math.max((int)Math.floor(fSbarSize * clip.w), 1);
			float fSbarScroll = Math.min(1f * scrollV / contentW, 1f);
			int iSbarScroll = (int)Math.ceil(fSbarScroll * clip.w);
			
			for (int i = 0; i < clip.w; i++)
				clip.put(i, clip.h - 1, ((clip.w - i) > iSbarScroll && (clip.w - i) <= iSbarScroll + iSbarSize) ? Borders.charsBar[/*focus ? */4/* : 3*/] : Borders.charsBar[1]);
		}
	}
	
	protected abstract void updateContent(View parent, SplitterView.Side side, Clip clip);
}