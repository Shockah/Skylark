package pl.shockah;

import java.io.IOException;
import java.io.InputStream;

public class BinBufferInputStream extends InputStream {
	protected final BinBuffer binb;
	
	public BinBufferInputStream(BinBuffer binb) {
		this.binb = binb;
	}
	
	public int read() throws IOException {
		if (binb.bytesLeft() > 0)
			return binb.readByte();
		return -1;
	}
}