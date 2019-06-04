package com.berzenin.backup.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.java.Log;

@Log
public class ClientAction {
	
	public static boolean getDiferenceBetweenServerAndClient (Set<String> clientFiles, Set<String> serverFiles) {
		if (!clientFiles.equals(serverFiles)) {
			return true;
		}		
		return false;
	}
	
	public static Set<String> getSetDiferenceBetweenServerAndClient(Set<String> list, Set<String> list2) {
	    Set<String> changes = list.stream().filter(o1 -> list2.stream().noneMatch(o2 -> o2.equals(o1)))
	            .collect(Collectors.toSet());
		System.out.println("--------------Changes files--------------------");
		changes.stream().sorted();
	    changes.forEach(x -> System.out.println("There are changes from client: "+x));
		return changes;
		
	}
	
	public static Set<String> getSetDiferenceBetweenClietnAndServer(Set<String> list, Set<String> list2) {
	    Set<String> changes = list.stream().filter(o1 -> list2.stream().noneMatch(o2 -> o2.equals(o1)))
	            .collect(Collectors.toSet());
		System.out.println("--------------Changes files--------------------");
		changes.stream().sorted();
	    changes.forEach(x -> System.out.println("There are changes from server: "+x));
		return changes;
		
	}
	
	public static boolean copyChangesForServer(Set<String> changes,Path workingDirectoryPath, ObjectOutputStream oos, ObjectInputStream ois) {
		changes.stream().forEach(f -> 
		{
			try {
				sendFile((Paths.get(workingDirectoryPath.toString(), f.substring(0, f.indexOf("{")-1))), oos, ois);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return false;		
	}
	
	public static void deleteFilesFromServer(Set<String> changes,Path workingDirectory, ObjectOutputStream oos, ObjectInputStream ois) {
		changes.stream().forEach(f -> 
		{
			try {
				deleteFile((Paths.get(workingDirectory.toString(), f.substring(0, f.indexOf("{")-1))), oos, ois);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static Set<String> getInformationAboutWorkingDirectoryFromServer (ObjectOutputStream dos, ObjectInputStream ois) {
		Set<String> listOfFiles = new HashSet<>();;
		try {
			listOfFiles = (Set<String>) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		System.out.println("--------------Server files--------------------");
		listOfFiles.stream().sorted().forEach((v) -> System.out.println("File server : "+v.toString()));
		return listOfFiles;
	}	

	public static Set<String> getInformationAboutWorkDirectoryState(Path workingDirectoryPath) throws IOException {
		Set<String> files = new HashSet<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(workingDirectoryPath)) {
		    for (Path file: stream) {
		    	files.add(file.getFileName()+ " " +Files.readAttributes(file, "size"));
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    System.err.println(x);
		}
		System.out.println("--------------Client files--------------------");
		files.stream().sorted().forEach((v) -> System.out.println("File client : "+v.toString()));
		return files;
	}

	public static String getClientName() throws IOException {
		String hostname = "Unknown";
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
			log.info("Comp name: " + hostname);
			return hostname;
		} catch (UnknownHostException ex) {
			System.out.println("Hostname can not be resolved");
		}
		return hostname;
	}

	public static boolean createDirectoryIfNotExist(Path path) throws IOException {
		if (Files.notExists(path)) {
			log.info("Target file \"" + path + "\" will be created.");
			Files.createFile(Files.createDirectories(path)).toFile();
			return true;
		}
		return false;
	}

	public static void sendFile(Path file, ObjectOutputStream dos, ObjectInputStream ois) throws IOException {
		dos.writeUTF("copyOrModify");
		dos.flush();
		dos.writeUTF(file.getFileName().toString());
		dos.flush();
		List<String> lines = Files.readAllLines(file);
		int count = lines.size();
		dos.writeInt(count);
		int line = 0;
		while (count > 0) {
			dos.writeUTF(lines.get(line));
			dos.flush();
			count--;
			line++;
		}
		log.info("File copy: " + file);
	}

	public static String deleteFile(Path file, ObjectOutputStream dos, ObjectInputStream ois) throws IOException {
		dos.writeUTF("delete");
		dos.flush();
		log.info("Send file name: " + file.toString());
		dos.writeUTF(file.getFileName().toString());
		dos.flush();
		log.info("File delete: " + file);
		return "";
	}
}
