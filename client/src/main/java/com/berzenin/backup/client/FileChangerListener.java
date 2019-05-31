package com.berzenin.backup.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	private WatchService watchService;
	private ClientAction clientAction;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Path workingDirectoryPath;
	
	FileChangerListener(Path workingDirectoryPath, ObjectOutputStream oos, ObjectInputStream ois) {
		this.workingDirectoryPath = workingDirectoryPath;
		this.oos = oos;
		this.ois = ois;
	}		

	public void requestForCheckChangesFromDirectory () {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			log.info("Set working directory: "+workingDirectoryPath);
			workingDirectoryPath.register(watchService,  
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
			    	Path pathForFile = Paths.get(workingDirectoryPath.toString(), event.context().toString());
			    	eventExecutor(event.kind().toString(), pathForFile);
			    }
			    key.reset();
			}
		} catch (InterruptedException | IOException e) {
			log.severe(e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	public void eventExecutor(String whatHappened, Path file) throws IOException {
		switch (whatHappened) {
		case "ENTRY_CREATE":
			clientAction.sendFile(file, oos, ois);
			log.info("ENTRY_CREATE");
			break;
		case "ENTRY_MODIFY":
			clientAction.sendFile(file, oos, ois);
			log.info("ENTRY_MODIFY");
			break;
		case "ENTRY_DELETE":
			clientAction.deleteFile(file, oos, ois);
			log.info("ENTRY_DELETE");
			break;
		}
	}

}
