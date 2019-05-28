package com.berzenin.backup.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import lombok.extern.java.Log;

@Log
public class FileChangerListener {
	
//	private String args;
//	
//	FileChangerListener (String args) {
//		this.args=args;
//	}
//	
//	public synchronized String woice () {
//		return args;
//	}
	
	private WatchService watchService;
	private Path path;
	private String backupDir;
	private Socket socket;
	private ClientAction clientAction;
	private int port;
	private String serverName;
	private DataOutputStream oos;
	private DataInputStream ois;
	private String dir;
	private String clientName;
	
	FileChangerListener(String backupDir, int port, String serverName, String clientName) {
		this.port = port;
		this.backupDir = backupDir;
		this.serverName = serverName;
		this.clientName = clientName;
		dir=backupDir.substring(backupDir.lastIndexOf('\\'))+"\\";
		try {
			requestForCheckChangesFromDirectory ();	
			log.info("Client connected to socket");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		

	public void requestForCheckChangesFromDirectory () {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			path = Paths.get(backupDir);
			log.info("Set working directory: "+path);
			path.register(watchService,  
					StandardWatchEventKinds.ENTRY_CREATE, 
		              StandardWatchEventKinds.ENTRY_DELETE, 
		                StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			log.severe(e.getStackTrace().toString());
			e.printStackTrace();
		}
		
		WatchKey key;
        try {
			while ((key = watchService.take()) != null) {
			    for (WatchEvent<?> event : key.pollEvents()) {
			    	log.info(
			          "Event kind:" + event.kind() 
			            + ". File affected: " + event.context() + ".");
			    	eventExecutor(event.kind().toString(), event.context().toString());
			    }
			    key.reset();
			}
		} catch (InterruptedException | IOException e) {
			log.severe(e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
	
	public void eventExecutor(String whatHappened, String fileName) throws IOException {
		socket = new Socket(serverName, port);
		try {
			oos = new DataOutputStream(socket.getOutputStream());
			ois = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String path = backupDir + '\\';
		switch (whatHappened) {
		case "ENTRY_CREATE":
			clientAction.sendFile(clientName+dir, path ,fileName, oos);
			log.info("ENTRY_CREATE");
			break;
		case "ENTRY_MODIFY":
			clientAction.sendFile(clientName+dir, path ,fileName, oos);
			log.info("ENTRY_MODIFY");
			break;
		case "ENTRY_DELETE":
			clientAction.deleteFile(clientName+dir, path ,fileName, oos);
			log.info("ENTRY_DELETE");
			break;
		}
	}

}
