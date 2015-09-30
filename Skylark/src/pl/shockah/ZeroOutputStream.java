package pl.shockah;

import java.io.IOException;
import java.io.OutputStream;

public class ZeroOutputStream extends OutputStream {
	public void write(int b) throws IOException { }
}