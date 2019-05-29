package com.berzenin.backup.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.java.Log;

@Log
public class ServerAction {
	
	public static Path setWorkingDirectory (Path path, String clientMachineName, Path rootPath) throws IOException {
		Path root = path.getRoot();		
		Path fullPath = Paths.get(rootPath.toString(), clientMachineName, root.relativize(path).toString());
		if (Files.notExists(fullPath)) {
			Files.createDirectories(fullPath);
			log.info("Target directory \"" + fullPath + "\" will be created.");
		}
		return fullPath;
	}

}
