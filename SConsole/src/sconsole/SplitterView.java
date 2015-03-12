package sconsole;

import sconsole.old.ConsoleViewSplitter.Side;

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
}