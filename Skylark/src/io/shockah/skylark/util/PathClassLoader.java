package io.shockah.skylark.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PathClassLoader extends ClassLoader {
	protected final Path[] paths;
	
	public PathClassLoader(Path... paths) {
		super();
		this.paths = paths;
	}
	
	public PathClassLoader(List<Path> paths) {
		super();
		this.paths = paths.toArray(new Path[0]);
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> clazz = super.findClass(name);
		if (clazz != null)
			return clazz;
		
		String[] split = name.split("\\.");
		split[split.length - 1] += ".class";
		
		L: for (Path path : paths) {
			Path current = path;
			if (!Files.exists(current))
				continue;
			
			for (String pathPart : split) {
				current = current.resolve(pathPart);
				if (!Files.exists(current))
					continue L;
			}
			
			try {
				byte[] bytes = Files.readAllBytes(current);
				return defineClass(name, bytes, 0, bytes.length);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}