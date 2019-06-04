package com.berzenin.backup.server;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.java.Log;

@Log
public class App {
	private int port;
	private Path rootPath;

	public App(Path rootPath, int port) {
		this.rootPath = rootPath;
		this.port = port;
		startClient(this);
	}

	public App() {
	}

	public static void main(String[] args) throws Exception {
		App app = new App();
		if (args.length == 3 && app.isArgsRight(args[1], args[0])) {
			app.port = Integer.parseInt(args[0]);
			app.rootPath = Paths.get(args[1]);
			app.startClient(app);
		} else {
			new JFrameForArgs().createGUI();
		}
	}

	private boolean isArgsRight(String workingDirectoryPath, String port) {
		if (workingDirectoryPath.equals(null) || workingDirectoryPath.length() == 0) {
			return false;
		}
		if (port.equals(null) || port.length() == 0) {
			return false;
		}
		return true;
	}

	private void startClient(App app) {
		log.info("Main working directory: " + app.rootPath);
		log.info("port: " + app.port);
		try {
			new Server(app.port, app.rootPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
