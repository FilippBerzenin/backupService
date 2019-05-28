package com.berzenin.backup.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
