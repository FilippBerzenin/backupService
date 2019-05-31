package com.berzenin.backup.server;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App 
{
	private int port;
	private Path rootPath;
	
    public static void main(String[] args) throws Exception {
    	App app = new App ();
		app.rootPath = Paths.get("c:\\server\\");
    	app.port = 3345; //stub
    	new Server(app.port, app.rootPath);
    }
}
