package com.berzenin.backup.client;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.java.Log;

@Log
public class App {

	private String serverIp;
	private Path workingDirectoryPath;
	private int port;
	private Client client;
	

	public App(String serverIp, Path workingDirectoryPath, int port) {
		this.serverIp = serverIp;
		this.workingDirectoryPath = workingDirectoryPath;
		this.port = port;
		startClient(this);
	}

	public App() {
	}

	public static void main(String[] args) {
		App app = new App();
		if (args.length == 3 && app.isArgsRight(args[0], args[1], args[2])) {
			app.workingDirectoryPath = Paths.get(args[0]);
			app.serverIp = args[1];
			app.port = Integer.parseInt(args[2]);
			app.startClient(app);
		} else {
			new JFrameForArgs().createGUI();;
		}
	}

	private boolean isArgsRight(String workingDirectoryPath, String serverIp, String port) {
		if (workingDirectoryPath.equals(null) || workingDirectoryPath.length() == 0) {
			return false;
		}
		if (serverIp.equals(null) || workingDirectoryPath.length() == 0) {
			return false;
		}
		if (port.equals(null) || port.length() == 0) {
			return false;
		} 
		return true;
	}
	
	private void startClient(App app) {
		log.info("workingDirectoryPath: " + app.workingDirectoryPath);
		log.info("serverIp: " + app.serverIp);
		log.info("port: " + app.port);
		app.client = new Client(app.workingDirectoryPath, app.serverIp, app.port);
		app.client.run();
	}
}
