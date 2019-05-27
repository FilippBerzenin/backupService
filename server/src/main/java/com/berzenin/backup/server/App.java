package com.berzenin.backup.server;

import java.nio.file.Paths;

public class App 
{
	private int port;
    public static void main(String[] args) throws Exception {
    	App app = new App ();
    	app.port = 3345;
    	Server server = new Server(app.port);
    }
}
