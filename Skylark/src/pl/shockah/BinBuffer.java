package pl.shockah;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BinBuffer {
	protected byte[] buffer;
	
	protected int pos = 0, bytes = 0;
	
	public BinBuffer() { this(8192); }
	public BinBuffer(int startBytes) {
		this(startBytes, false);
	}
	public BinBuffer(int startBytes, boolean filled) {
		buffer = new byte[startBytes];
		if (filled)
			bytes = startBytes;
	}
	public BinBuffer(ByteBuffer bb) {
		this(bb, false);
	}
	public BinBuffer(ByteBuffer bb, boolean filled) {
		if (filled) {
			setByteBuffer(bb);
			setPos(bytes);
		} else {
			buffer = bb.array();
		}
	}
	public BinBuffer(byte[] bytes) {
		this(bytes, false);
	}
	public BinBuffer(byte[] bytes, boolean filled) {
		if (filled)
			this.bytes = bytes.length;
		buffer = bytes;
	}
	
	protected void increaseBufferSize() {
		buffer = Arrays.copyOf(buffer, buffer.length * 2);
	}
	
	public int getPos() { return pos; }
	public void setPos(int pos) { this.pos = Math.min(Math.max(pos, 0), bytes); }
	
	public boolean isEmpty() { return getSize() == 0; }
	public int getSize() { return bytes; }
	public int getSizeReal() { return buffer.length; }
	public int bytesLeft() { return bytes - pos; }
	
	public void clear() {
		pos = 0;
		bytes = 0;
	}
	
	public BinBuffer writeByte(int value) {
		if (pos > buffer.length-1)
			increaseBufferSize();
		if (pos > bytes-1)
			bytes++;
		int val = value % 256;
		if (val < 0)
			val += 256;
		buffer[pos++] = (byte)val;
		return this;
	}
	protected void writeByte(long value) {
		long val = value % 256;
		if (val < 0)
			val += 256;
		writeByte((int)val);
	}
	public BinBuffer writeUXBytes(long value, int x) {
		for (int i = 0; i < x; i++) {
			writeByte(value);
			value >>= 8;
		}
		return this;
	}
	public BinBuffer writeXBytes(long value, int x) { writeUXBytes(value + ((long)(Math.pow(256, x)) >> 1), x); return this; }
	
	public BinBuffer writeSByte(int value) { writeXBytes(value, 1); return this; }
	public BinBuffer writeUShort(int value) { writeUXBytes(value, 2); return this; }
	public BinBuffer writeShort(int value) { writeXBytes(value, 2); return this; }
	public BinBuffer writeUInt(long value) { writeUXBytes(value, 4); return this; }
	public BinBuffer writeInt(int value) { writeXBytes(value, 4); return this; }
	public BinBuffer writeFloat(float value) { writeUXBytes(Float.floatToRawIntBits(value), 4); return this; }
	public BinBuffer writeDouble(double value) { writeUXBytes(Double.doubleToRawLongBits(value), 8); return this; }
	public BinBuffer writeChars(String text) {
		for (int i = 0; i < text.length(); i++)
			writeByte(text.charAt(i));
		return this;
	}
	public BinBuffer writeUChars(String text) {
		for (int i = 0; i < text.length(); i++)
			writeUShort(text.charAt(i));
		return this;
	}
	public BinBuffer writeString(String text) {
		writeUInt(text.length());
		writeChars(text);
		return this;
	}
	public BinBuffer writeUString(String text) {
		writeUInt(text.length());
		writeUChars(text);
		return this;
	}
	public BinBuffer writeJavaString(String text) {
		byte[] bytes = text.getBytes(Charset.forName("UTF-8"));
		writeUShort(bytes.length);
		writeBytes(bytes);
		return this;
	}
	public BinBuffer writeBytes(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++)
			writeSByte(bytes[i]);
		return this;
	}
	public BinBuffer writeObject(IBinBufferSerializable ibbs) { ibbs.getSerializer().write(this, ibbs); return this; }
	
	public int readByte() {
		if (pos > buffer.length-1)
			return 0;
		return buffer[pos++] & 0xff;
	}
	public long readUXBytes(int x) {
		long value = 0;
		for (int i = 0; i < x; i++)
			value += readByte()*Math.pow(256,i);
		return value;
	}
	public long readXBytes(int x) { return readUXBytes(x) - ((long)(Math.pow(256, x)) >> 1); }
	
	public int readSByte() { return (int)readXBytes(1); }
	public int readUShort() { return (int)readUXBytes(2); }
	public int readShort() { return (int)readXBytes(2); }
	public long readUInt() { return readUXBytes(4); }
	public int readInt() { return (int)readXBytes(4); }
	public float readFloat() { return Float.intBitsToFloat((int)readUXBytes(4)); }
	public double readDouble() { return Double.longBitsToDouble(readUXBytes(8)); }
	public String readChars(int length) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < length; i++)
			s.append((char)readByte());
		return s.toString();
	}
	public String readUChars(int length) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < length; i++)
			s.append((char)readUShort());
		return s.toString();
	}
	public String readString() { return readChars((int)readUInt()); }
	public String readUString() { return readUChars((int)readUInt()); }
	public String readJavaString() { return new String(readBytes(readUShort()), Charset.forName("UTF-8")); }
	public byte[] readBytes(int bytes) {
		byte[] ret = new byte[bytes];
		for (int i = 0; i < bytes; i++)
			ret[i] = (byte)readSByte();
		return ret;
	}
	public IBinBufferSerializable readObject(BinBufferSerializer bbs) { return bbs.read(this); }
	
	public void fillByte(int value) {
		for (int i = 0; i < getSizeReal(); i++)
			writeByte(value);
	}
	
	public BinBuffer copy() { return copy(bytesLeft()); }
	public BinBuffer copy(int bytes) {
		BinBuffer binb = new BinBuffer();
		for (int i = 0; i < bytes; i++)
			binb.writeByte(readByte());
		return binb;
	}
	
	public void writeBinBuffer(BinBuffer binb) { writeBinBuffer(binb, binb.bytesLeft()); }
	public void writeBinBuffer(BinBuffer binb, int bytes) {
		for (int i = 0; i < bytes; i++)
			writeByte(binb.readByte());
	}
	
	public ByteBuffer getByteBuffer() {
		return ByteBuffer.wrap(buffer, getPos(), getSize()).asReadOnlyBuffer();
	}
	public BinBuffer setByteBuffer(ByteBuffer bb) {
		buffer = bb.array();
		bytes = bb.limit();
		return this;
	}
}