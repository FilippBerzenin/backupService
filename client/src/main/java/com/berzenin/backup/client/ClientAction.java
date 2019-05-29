package com.berzenin.backup.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import lombok.extern.java.Log;

@Log
public class ClientAction {

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
	
	public static boolean createDirectoryIfNotExist(Path path) throws IOException  {
		if (Files.notExists(path)) {
			log.info("Target file \"" + path + "\" will be created.");
			Files.createFile(Files.createDirectories(path)).toFile();
			return true;
		}
		return false;		
	}
	
	public static void sendFile(Path file, DataOutputStream dos, DataInputStream ois) throws IOException {
//	    File myFile = new File(file);
		dos.writeUTF("copyOrModify");
		dos.flush();
//	    while (true) {
//	      Socket sock = servsock.accept();
//	    	System.out.println("File name: "+file);
//	    	System.out.println("File path: "+file.getFileName());
//	    	System.out.println(file.toFile().length());
	    	dos.writeUTF(file.getFileName().toString());
	    	dos.flush();
	    	List<String> lines = Files.readAllLines(file);
	    	int count = lines.size();
	    	dos.writeInt(count);	    	
	    	int line = 0;
	    	while (count>0) {
	    		dos.writeUTF(lines.get(line));
	    		dos.flush();
	    		count--;
	    		line++;
	    	}
//    		dos.writeUTF("break");
//    		dos.flush();
//	    	try (
//	    	    BufferedReader reader =
//	    	      new BufferedReader(Files.newBufferedReader(file))) {
//	    	    String line = null;
//	    	    while ((line = reader.readLine()) != null) {
//	    	        System.out.println(line);
//	    	    }
//	    	} catch (IOException x) {
//	    	    System.err.println(x);
//	    	}
//	    	Files.copy(file, dos);
//	    	dos.flush();
	    	log.info("File copy: "+file);
//	      byte[] mybytearray = new byte[(int) file.toFile().length()];
//	      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.toFile()));
//	      bis.read(mybytearray, 0, mybytearray.length);
////	      OutputStream os = sock.getOutputStream();
//	      dos.write(mybytearray, 0, mybytearray.length);
//	      dos.flush();
//	    }
//	      sock.close();
//		dos.writeUTF("copyOrModify");
//		dos.flush();
//		log.info("Send path: "+dir);
//		dos.writeUTF(dir);
//		dos.flush();
//		log.info("Send file name: "+file);
//		dos.writeUTF(file);
//		dos.flush();
//		Path tempFile = Files.createTempFile(file);
//		Files.copy(ois, tempFile);
//		FileInputStream fis = new FileInputStream(file);
//		byte[] buffer = new byte[4096];
//		while (fis.read(buffer) > -1) {
//			dos.write(buffer);
//		}
//		log.info("File "+file+" copy for server");
//		dos.close();
//		fis.close();
	}
	
	public static String deleteFile(String dir, String path, String fileName, DataOutputStream dos) throws IOException {
		dos.writeUTF("delete");
		dos.flush();
		log.info("Send path: "+dir);
		dos.writeUTF(dir);
		dos.flush();
		log.info("Send file name: "+fileName);
		dos.writeUTF(fileName);
		dos.flush();
		log.info("File delete: " + fileName);
		return "";
	}
}
