package com.berzenin.backup.client;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
	private String serverIp;
	private Path workingDirectoryPath;
	private int port;
	private Client client;

	public static void main(String[] args) throws Exception {
		App app = new App();
		if (args.length != 3) {
			System.err.println("Pass arguments from command line...");
			app.serverIp = "localhost";
			app.workingDirectoryPath = Paths.get("c:\\client\\dir2\\");
			app.port = 3345;
		} else {
			app.workingDirectoryPath = Paths.get(args[0]);
			app.serverIp = args[1];
			app.port = Integer.parseInt(args[2]);
		}
		app.client = new Client(app.workingDirectoryPath, app.serverIp, app.port);
		app.client.run();
	}
}
