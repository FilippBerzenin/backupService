package com.berzenin.backup.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Set;

import lombok.extern.java.Log;

@Log
public class Client {

	private String serverAddress;
	private Path workingDirectoryPath;
	private int port;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public Client(Path workingDirectoryPath, String serverAddress, int port) {
		this.workingDirectoryPath = workingDirectoryPath;
		this.serverAddress = serverAddress;
		this.port = port;
	}

	public void run() throws IOException, InterruptedException {
		try (Socket socket = new Socket(serverAddress, port)) {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			swopFirstInformationWithServer();
			FileChangerListener fileChangerListener = new FileChangerListener(workingDirectoryPath, oos, ois);

			while (true) {
				fileChangerListener.requestForCheckChangesFromDirectory();
				Thread.sleep(50);
			}
		} catch (ConnectException e) {
			log.warning("Something with the connection, the server may not be turned on");
			System.exit(0);
		}
	}

	private void swopFirstInformationWithServer() {
		try {
			log.info("Сheck connection to server...Sending - Hello!");
			oos.writeUTF("Hello!");
			oos.flush();
			String ansver = ois.readUTF();
			log.info("Server echo - " + ansver);

			log.info("Server send workingDirectoryPath - " + workingDirectoryPath);
			if (ClientAction.createDirectoryIfNotExist(workingDirectoryPath)) {
				log.info("Working directory created - " + workingDirectoryPath);
			}
			oos.writeUTF(workingDirectoryPath.toString());
			oos.flush();

			String clientMachine = ClientAction.getClientName();
			log.info("Server send clientMachine - " + clientMachine);
			oos.writeUTF(clientMachine);
			oos.flush();
			Set<String> clientFiles = ClientAction.getInformationAboutWorkDirectoryState(workingDirectoryPath);
			if (ois.readUTF().equals("sendInformationAboutDirectoryState")) {
				Set<String> serverFiles = ClientAction.getInformationAboutWorkingDirectoryFromServer(oos, ois);
				if (ClientAction.getDiferenceBetweenServerAndClient(clientFiles, serverFiles)) {
					Set<String> changes = ClientAction.getSetDiferenceBetweenServerAndClient(clientFiles, serverFiles);
					ClientAction.copyChangesForServer(changes, workingDirectoryPath, oos, ois);
				}
			}
		} catch (IOException e) {
			e.getStackTrace();
		}
	}
}
