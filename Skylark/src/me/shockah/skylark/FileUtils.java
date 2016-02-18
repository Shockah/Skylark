package me.shockah.skylark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;

public final class FileUtils {
	public static Path createTrueTempFile(FileAttribute<?>... attrs) throws IOException {
		return createTrueTempFile(null, null, attrs);
	}
	
	public static Path createTrueTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
		Path tmp = Files.createTempFile(prefix, suffix, attrs);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					Files.delete(tmp);
				} catch (IOException e) {
				}
			}
		});
		return tmp;
	}
	
	public static Path copyAsTrueTempFile(Path source, FileAttribute<?>... attrs) throws IOException {
		String suffix = null;
		String fname = source.getFileName().toString();
		if (fname.contains(".")) {
			String[] split = fname.split("\\.");
			suffix = "." + split[split.length - 1];
		}
		return copyAsTrueTempFile(source, null, suffix, attrs);
	}
	
	public static Path copyAsTrueTempFile(Path source, String prefix, String suffix, FileAttribute<?>... attrs) throws IOException {
		Path tmp = createTrueTempFile(prefix, suffix, attrs);
		Files.copy(source, tmp, StandardCopyOption.REPLACE_EXISTING);
		return tmp;
	}
	
	private FileUtils() {
		throw new UnsupportedOperationException();
	}
}