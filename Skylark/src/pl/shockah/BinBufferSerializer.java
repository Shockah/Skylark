package pl.shockah;

public abstract class BinBufferSerializer {
	public abstract void write(BinBuffer binb, IBinBufferSerializable ibbs);
	public abstract IBinBufferSerializable read(BinBuffer binb);
}