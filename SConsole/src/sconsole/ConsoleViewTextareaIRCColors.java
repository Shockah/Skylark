package sconsole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.shockah.Box;
import pl.shockah.StrBuffer;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class ConsoleViewTextareaIRCColors extends ConsoleViewTextarea {
	public static final String
		REGEX_COLOR = "([0-9]{1,2})";
	public static final Pattern
		COLOR_FG_BG = Pattern.compile(REGEX_COLOR + "," + REGEX_COLOR),
		COLOR_FG_RESETBG = Pattern.compile(REGEX_COLOR + ","),
		COLOR_BG_RESETFG = Pattern.compile("," + REGEX_COLOR),
		COLOR_FG = Pattern.compile(REGEX_COLOR);
	public static final Color[] colors = {
		Color.WHITE, Color.BLACK, Color.BLUE, Color.GREEN,
		Color.RED, Color.RED, Color.MAGENTA, Color.YELLOW,
		Color.YELLOW, Color.GREEN, Color.CYAN, Color.CYAN,
		Color.BLUE, Color.MAGENTA, Color.BLACK, Color.WHITE
	};
	
	public ConsoleViewTextareaIRCColors(ConsoleThread thread) {
		super(thread);
	}
	public ConsoleViewTextareaIRCColors(ConsoleThread thread, int scrollback) {
		super(thread, scrollback);
	}
	
	public void drawLine(int x, int y, String line, Box<Color> colorBackground, Box<Color> colorForeground) {
		Color baseBackground = colorBackground.value;
		Color baseForeground = colorForeground.value;
		
		StrBuffer strb = new StrBuffer();
		strb.append(line);
		strb.setPos(0);
		
		while (!strb.atEnd()) {
			char c = strb.readChar();
			switch (c) {
				case 3:
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 5; i++) {
						if (strb.atEnd()) {
							break;
						} else {
							sb.append(strb.readChar());
						}
					}
					strb.seek(-sb.length());
					
					Matcher m = COLOR_FG_BG.matcher(sb);
					if (m.find() && m.start() == 0) {
						colorForeground.value = colors[Integer.parseInt(m.group(1))];
						colorBackground.value = colors[Integer.parseInt(m.group(2))];
						strb.seek(m.end() - m.start());
					} else {
						m = COLOR_FG_RESETBG.matcher(sb);
						if (m.find() && m.start() == 0) {
							colorForeground.value = colors[Integer.parseInt(m.group(1))];
							colorBackground.value = baseBackground;
							strb.seek(m.end() - m.start());
						} else {
							m = COLOR_BG_RESETFG.matcher(sb);
							if (m.find() && m.start() == 0) {
								colorBackground.value = colors[Integer.parseInt(m.group(1))];
								colorForeground.value = baseForeground;
								strb.seek(m.end() - m.start());
							} else {
								m = COLOR_FG.matcher(sb);
								if (m.find() && m.start() == 0) {
									colorForeground.value = colors[Integer.parseInt(m.group(1))];
									strb.seek(m.end() - m.start());
								} else if (sb.length() != 0 && sb.charAt(0) == ',') {
									colorForeground.value = baseForeground;
									colorBackground.value = baseBackground;
									strb.seek(1);
								} else {
									colorForeground.value = baseForeground;
									colorBackground.value = baseBackground;
								}
							}
						}
					}
					
					break;
				case 15:
					colorForeground.value = baseForeground;
					colorBackground.value = baseBackground;
					break;
				default:
					rect.draw(x++, y, c, colorForeground.value, colorBackground.value);
					break;
			}
		}
	}
	
	public int length(CharSequence s) {
		StrBuffer strb = new StrBuffer();
		strb.append(s);
		strb.setPos(0);
		
		int length = 0;
		while (!strb.atEnd()) {
			char c = strb.readChar();
			switch (c) {
				case 3:
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < 5; i++) {
						if (strb.atEnd()) {
							break;
						} else {
							sb.append(strb.readChar());
						}
					}
					strb.seek(-sb.length());
					
					Matcher m = COLOR_FG_BG.matcher(sb);
					if (m.find() && m.start() == 0) {
						strb.seek(m.end() - m.start());
					} else {
						m = COLOR_FG_RESETBG.matcher(sb);
						if (m.find() && m.start() == 0) {
							strb.seek(m.end() - m.start());
						} else {
							m = COLOR_BG_RESETFG.matcher(sb);
							if (m.find() && m.start() == 0) {
								strb.seek(m.end() - m.start());
							} else {
								m = COLOR_FG.matcher(sb);
								if (m.find() && m.start() == 0) {
									strb.seek(m.end() - m.start());
								} else if (sb.length() != 0 && sb.charAt(0) == ',') {
									strb.seek(1);
								}
							}
						}
					}
					
					break;
				case 15:
					break;
				default:
					length++;
					break;
			}
		}
		
		return length;
	}
}