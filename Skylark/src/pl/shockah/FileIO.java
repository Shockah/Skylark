package pl.shockah;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public final class FileIO {
	public static byte[] readAllBytes(File file) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			byte[] ret = new byte[(int)file.length()];
			bis.read(ret);
			return ret;
		}
	}
	
	public static String readWholeString(File file) throws IOException {
		return readWholeString(new BufferedInputStream(new FileInputStream(file)));
	}
	public static String readWholeString(InputStream is) throws IOException {
		try (Scanner scanner = new Scanner(is)) {
			scanner.useDelimiter("\\Z");
			return scanner.next();
		}
	}
	
	public static void writeAllBytes(File file, byte[] bytes) throws IOException {
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
			bos.write(bytes);
		}
	}
	
	public static void writeWholeString(File file, String s) throws IOException {
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.print(s);
		}
	}
}