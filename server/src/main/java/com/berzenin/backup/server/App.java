package com.berzenin.backup.server;

public class App 
{
	private int port;
    public static void main(String[] args) throws Exception {
    	App app = new App ();
    	app.port = 3345; //stub
    	Server server = new Server(app.port);
    }
}
