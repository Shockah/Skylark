package me.shockah.skylark;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginManager {
	public static final Path pluginsPath = Paths.get("plugins");
	
	protected List<Plugin> plugins = Collections.synchronizedList(new ArrayList<>());
	
	public void load() {
		
	}
	
	protected ClassLoader createClassLoader() {
		try {
			List<Path> tmpPluginPaths = new ArrayList<>();
			for (Path path : Files.newDirectoryStream(pluginsPath, (path) -> path.getFileName().toString().endsWith(".jar"))) {
				tmpPluginPaths.add(FileUtils.copyAsTrueTempFile(path));
			}
			
			List<Path> fsPaths = new ArrayList<>();
			for (Path path : tmpPluginPaths) {
				fsPaths.add(FileSystems.newFileSystem(path, null).getPath("/"));
			}
			
			return new PathClassLoader(fsPaths);
		} catch (Exception e) {
			return null;
		}
	}
}