package pl.shockah;

import java.io.IOException;
import java.io.OutputStream;

public class BinBufferOutputStream extends OutputStream {
	protected final BinBuffer binb;
	
	public BinBufferOutputStream(BinBuffer binb) {
		this.binb = binb;
	}
	
	public void write(int b) throws IOException {
		binb.writeByte(b);
	}
}