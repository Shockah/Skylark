package sconsole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultilineTextScrollView extends ScrollView {
	public static final String DEFAULT_INDENT = "  ";
	
	protected List<String> lines = Collections.synchronizedList(new ArrayList<String>());
	protected List<String> cachedLines = new ArrayList<>();
	
	public MultilineTextScrollView(Console console, Clip clip) {
		super(console, clip);
	}
	
	protected List<String> linewrap(Clip clip, String line) {
		return linewrap(clip, line, DEFAULT_INDENT);
	}
	protected synchronized List<String> linewrap(Clip clip, String line, String indent) {
		List<String> list = new ArrayList<>();
		if (clip.w <= 0 || clip.h <= 0)
			return list;
		
		String[] spl = line.split("((?<=\\s)|(?=\\s))");
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < spl.length; i++) {
			String s = spl[i];
			int len = length(sb);
			int len2 = length(s);
			if (len + len2 <= clip.w || len == 0) {
				sb.append(s);
			} else {
				list.add(sb.toString());
				sb = new StringBuilder(indent);
				if (!s.matches("\\s"))
					sb.append(s);
			}
		}
		
		return list;
	}
	public int length(CharSequence s) {
		return s.length();
	}
	
	protected synchronized void recache() {
		Clip clip = contentClip();
		synchronized (cachedLines) {
			cachedLines.clear();
			synchronized (lines) {
				for (String line : lines)
					cachedLines.addAll(linewrap(clip, line));
			}
		}
		contentH = cachedLines.size();
	}
	
	protected void updateContent(View parent, SplitterView.Side side, Clip clip) {
		for (int i = 0; i < clip.h && i + scrollH < cachedLines.size(); i++)
			clip.put(0, i, cachedLines.get(i + scrollH));
	}
}