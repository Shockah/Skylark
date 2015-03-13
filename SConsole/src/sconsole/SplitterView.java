package sconsole;

public class SplitterView extends View {
	public static enum Side {
		Left(true), Right(true), Top(false), Bottom(false);
		
		public final boolean h, v;
		
		private Side(boolean h) {
			this.h = h;
			this.v = !h;
		}
		
		public Side opposite() {
			switch (this) {
				case Left: return Right;
				case Right: return Left;
				case Top: return Bottom;
				case Bottom: return Top;
			}
			return null;
		}
	}
	
	public View mainView = null, offView = null;
	public Side side = null;
	public int size = 0;
	
	public SplitterView(Console console, Clip clip) {
		super(console, clip);
	}
	
	public void setMainView(View view) {
		setMainView(view, side, size);
	}
	public void setMainView(View view, Side side, int size) {
		mainView = view;
		this.side = side;
		this.size = size;
		requestUpdate();
	}
	
	public void setOffView(View view) {
		offView = view;
		requestUpdate();
	}
	
	public void setViews(View mainView, View offView, Side side, int size) {
		this.mainView = mainView;
		this.offView = offView;
		this.side = side;
		this.size = size;
		requestUpdate();
	}
	
	protected void update(Side side) {
		if (mainView == null ^ offView == null) {
			View view = mainView == null ? offView : mainView;
			view.clip.x = view.clip.y = 0;
			view.clip.w = clip.w;
			view.clip.h = clip.h;
			view.update(null);
		} else if (mainView == null) {
			clear();
		} else {
			mainView.recalcMinSize(this.side);
			
			if (this.side.h) {
				mainView.clip.w = Math.max(mainView.minSize, this.size);
				offView.clip.w = clip.w - mainView.clip.w - 1;
				
				mainView.clip.y = offView.clip.y = 0;
				mainView.clip.h = offView.clip.h = clip.h;
			} else {
				mainView.clip.h = Math.max(mainView.minSize, this.size);
				offView.clip.h = clip.h - mainView.clip.h - 1;
				
				mainView.clip.x = offView.clip.x = 0;
				mainView.clip.w = offView.clip.w = clip.w;
			}
			
			switch (this.side) {
				case Left:
					mainView.clip.x = 0;
					offView.clip.x = mainView.clip.w + 1;
					break;
				case Right:
					offView.clip.x = 0;
					mainView.clip.x = offView.clip.w + 1;
					break;
				case Top:
					mainView.clip.y = 0;
					offView.clip.y = mainView.clip.h + 1;
					break;
				case Bottom:
					offView.clip.y = 0;
					mainView.clip.y = offView.clip.h + 1;
					break;
			}
			
			mainView.clip.fix();
			offView.clip.fix();
			
			mainView.update(this.side);
			offView.update(this.side.opposite());
		}
	}
}