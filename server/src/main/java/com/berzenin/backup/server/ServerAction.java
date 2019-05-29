package com.berzenin.backup.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;

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
	
	public static void copyFilesForDirectory(Path workingDirectoryPath, DataInputStream in, DataOutputStream oos) throws IOException {
		String lineSeparator = System.getProperty("line.separator");
		String fileName = in.readUTF();
	    Path path = Paths.get(workingDirectoryPath.toString(), fileName);
		log.info("Path: " + path.toString());
		FileOutputStream fos = new FileOutputStream(path.toString());

		int lines = in.readInt();
		while (lines>0) {
			fos.write(in.readUTF().getBytes());
			fos.write(lineSeparator.getBytes());
			lines--;
			
		}
		fos.close();
//		Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
//	    File copied = new File(path.toString());
//	    	    FileUtils.copyFile(in, path);
		log.info("File name for copy: " + workingDirectoryPath.toString());
//		createDirectoryIfNotExist(getWorkingPath(fullPass));
//		FileOutputStream fos = new FileOutputStream(fullPass);
//		byte[] buffer = new byte[4096];
//        int count;
//        while ((count = in.read(buffer)) > 0) {
//        	fos.write(buffer, 0, count);
//        }
//		fos.close();
//		in.close();
}

}
