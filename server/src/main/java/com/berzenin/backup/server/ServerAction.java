package com.berzenin.backup.server;

import static java.nio.file.StandardOpenOption.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

import lombok.extern.java.Log;

@Log
public class ServerAction {

	public static Path setWorkingDirectory(Path path, String clientMachineName, Path rootPath) {
		Path root = path.getRoot();
		Path fullPath = Paths.get(rootPath.toString(), clientMachineName, root.relativize(path).toString());
		if (Files.notExists(fullPath)) {
			try {
				Files.createDirectories(fullPath);
			} catch (IOException e) {
				e.printStackTrace();
				log.warning(e.toString());
			}
			log.info("Target directory \"" + fullPath + "\" will be created.");
		}
		return fullPath;
	}

	public static void copyFilesForDirectory(Path workingDirectoryPath, ObjectInputStream in, ObjectOutputStream oos)
			throws IOException {
		String lineSeparator = System.getProperty("line.separator");
		String fileName = in.readUTF();
		Path path = Paths.get(workingDirectoryPath.toString(), fileName);
		log.info("Path: " + path.toString());
		FileOutputStream fos = new FileOutputStream(path.toString());
		int lines = in.readInt();
		while (lines > 0) {
			fos.write(in.readUTF().getBytes());
			lines--;
			if (lines == 0) {
				break;
			}
			else fos.write(lineSeparator.getBytes());
		}
		fos.close();
		log.info("File name for copy: " + workingDirectoryPath.toString());
	}

	public static void deleteFilesForDirectory(Path workingDirectoryPath, ObjectInputStream in,
			ObjectOutputStream oos) {
		String fileName;
		try {
			fileName = in.readUTF();
			Path path = Paths.get(workingDirectoryPath.toString(), fileName);
			log.info("Path: " + path.toString());
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
			log.warning(e.toString());
		}
		log.info("File name for delete: " + workingDirectoryPath.toString());
	}

	public static Set<String> getInformationAboutWorkDirectoryState(Path workingDirectoryPath) {
		Set<String> files = new HashSet<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(workingDirectoryPath)) {
		    for (Path file: stream) {
		    	files.add(file.getFileName()+ " " +Files.readAttributes(file, "size"));
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    System.err.println(x);
		}		
		files.forEach((v) -> System.out.println("File : "+v.toString()));
		return files;
	}
	
	public static void sendInformationAboutWorkDirectoryStateForClient (Set<String> files, ObjectInputStream in, ObjectOutputStream oos) {
		try {
			oos.writeObject(files);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			log.warning(e.toString());
		}
	}
}
