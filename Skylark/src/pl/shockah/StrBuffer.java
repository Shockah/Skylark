package pl.shockah;

public class StrBuffer {
	protected String s = "";
	protected int pos = 0;
	
	public String toString() {
		return s;
	}
	
	public int getPos() {
		return pos;
	}
	
	public void setPos(int pos) {
		this.pos = Math.min(Math.max(pos, 0), s.length());
	}
	
	public void seek(int seek) {
		pos += seek;
	}
	
	public boolean atEnd() {
		return pos == s.length();
	}
	
	public boolean isEmpty() {
		return s.equals("");
	}
	
	public int getSize() {
		return s.length();
	}
	
	public void clear() {
		pos = 0;
		s = "";
	}
	
	public void append(Object o) {
		String s2 = "";
		if (pos > 0)
			s2 += s.substring(0, pos);
		s2 += o.toString();
		if (pos < s.length() - 1)
			s2 += s.substring(pos);
		
		pos += s2.length() - s.length();
		s = s2;
	}
	public void delete(int len) {
		String s2 = "";
		if (pos > 0)
			s2 += s.substring(0, pos);
		s2 += s.substring(pos + len);
		
		s = s2;
	}
	
	public char readChar() {
		return s.charAt(pos++);
	}
}