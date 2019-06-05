package com.berzenin.backup.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.java.Log;

@Log
public class Server {

	Server(int port, Path rootPath) throws IOException {
		System.out.println("The backup server is running...");
		ExecutorService pool = Executors.newFixedThreadPool(50);
		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				pool.execute(new Handler(listener.accept(), rootPath));
			}
		}
	}

	private static class Handler extends Thread {
		private Socket socket;
		private ObjectOutputStream oos;
		private ObjectInputStream ois;
		private Path workingDirectoryPath;
		private String clientMachine;
		private String command;
		private Path rootPath;
		

		public Handler(Socket socket, Path rootPath) {
			setDaemon(true);
			this.socket = socket;
			this.rootPath = rootPath;
			
		}

		public void run() {
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				swopFirstInformationWithClient();
			} catch (IOException e) {
				e.getStackTrace();
			}
		}

		private void swopFirstInformationWithClient() {
			command = "no command";
			try {
				String hello = ois.readUTF();
				log.info("Client send - " + hello);
				oos.writeUTF(hello);
				oos.flush();
				log.info("Server get command - " + command);
				workingDirectoryPath = Paths.get(ois.readUTF());
				log.info("Server get command - " + command);
				clientMachine = ois.readUTF();
				workingDirectoryPath = ServerAction.setWorkingDirectory(workingDirectoryPath, clientMachine, rootPath);
				Set<String> files = ServerAction.getInformationAboutWorkDirectoryState(workingDirectoryPath);
				oos.writeUTF("sendInformationAboutDirectoryState");
				oos.flush();
				ServerAction.sendInformationAboutWorkDirectoryStateForClient(files, ois, oos);
				doActionWithFilesSystem();
			} catch (IOException e) {
				e.getStackTrace();
			}
		}

		private void doActionWithFilesSystem() throws IOException {
			while (true) {
				String command = ois.readUTF();
				switch (command) {
				case ("copyOrModify"):
					ServerAction.copyFilesForDirectory(workingDirectoryPath, ois, oos);
					break;
				case ("delete"):
					ServerAction.deleteFilesForDirectory(workingDirectoryPath, ois, oos);
					break;
				}

			}
		}
	}
}
