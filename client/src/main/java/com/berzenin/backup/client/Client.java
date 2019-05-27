package com.berzenin.backup.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import lombok.var;
import lombok.extern.java.Log;

/**
 * A simple Swing-based client for the chat server. Graphically it is a frame
 * with a text field for entering messages and a textarea to see the whole
 * dialog.
 *
 * The client follows the following Chat Protocol. When the server sends
 * "SUBMITNAME" the client replies with the desired screen name. The server will
 * keep sending "SUBMITNAME" requests as long as the client submits screen names
 * that are already in use. When the server sends a line beginning with
 * "NAMEACCEPTED" the client is now allowed to start sending the server
 * arbitrary strings to be broadcast to all chatters connected to the server.
 * When the server sends a line beginning with "MESSAGE" then all characters
 * following this string should be displayed in its message area.
 */

@Log
public class Client {

	private String serverAddress;
	private Path workingDirectoryPath;
	private int port;
	
	private Scanner in;
	private PrintWriter out;
		
	private DataOutputStream oos;
	private DataInputStream ois;	

//    JFrame frame = new JFrame("Chatter");
//    JTextField textField = new JTextField(50);
//    JTextArea messageArea = new JTextArea(16, 50);

	/**
	 * Constructs the client by laying out the GUI and registering a listener with
	 * the textfield so that pressing Return in the listener sends the textfield
	 * contents to the server. Note however that the textfield is initially NOT
	 * editable, and only becomes editable AFTER the client receives the
	 * NAMEACCEPTED message from the server.
	 */
	public Client(Path workingDirectoryPath, String serverAddress, int port) {
		this.workingDirectoryPath = workingDirectoryPath;
		this.serverAddress = serverAddress;
		this.port = port;
		

//        textField.setEditable(false);
//        messageArea.setEditable(false);
//        frame.getContentPane().add(textField, BorderLayout.SOUTH);
//        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
//        frame.pack();
//
//        // Send on enter then clear to prepare for next message
//        textField.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                out.println(textField.getText());
//                textField.setText("");
//            }
//        });
	}

//    private String getName() {
//        return JOptionPane.showInputDialog(
//            frame,
//            "Choose a screen name:",
//            "Screen name selection",
//            JOptionPane.PLAIN_MESSAGE
//        );
//    }

	public void run() throws IOException {
		try (var socket = new Socket(serverAddress, port))
				 {
			oos = new DataOutputStream(socket.getOutputStream());
			ois = new DataInputStream(socket.getInputStream());
//			in = new Scanner(socket.getInputStream());
//			out = new PrintWriter(socket.getOutputStream(), true);
			swopFirstInformationWithServer();
		} catch (ConnectException e) {
			log.warning("Something with the connection, the server may not be turned on");
			System.exit(0);
		}
		
//        try {
//            var socket = new Socket(serverAddress, 59001);
//            in = new Scanner(socket.getInputStream());
//            out = new PrintWriter(socket.getOutputStream(), true);
//
//            while (in.hasNextLine()) {
//                var line = in.nextLine();
//                if (line.startsWith("SUBMITNAME")) {
//                    out.println(getName());
//                } else if (line.startsWith("NAMEACCEPTED")) {
//                    this.frame.setTitle("Chatter - " + line.substring(13));
//                    textField.setEditable(true);
//                } else if (line.startsWith("MESSAGE")) {
//                    messageArea.append(line.substring(8) + "\n");
//                }
//            }
//        } finally {
//            frame.setVisible(false);
//            frame.dispose();
//        }
	}
	
	private void swopFirstInformationWithServer() {
		try {
			log.info("Сheck connection to server...Sending - Hello!");
			oos.writeUTF("Hello!");
			oos.flush();
			String ansver = ois.readUTF();
			log.info("Server echo - "+ansver);
			
			log.info("Server send workingDirectoryPath - "+workingDirectoryPath);
			if (ClientAction.createDirectoryIfNotExist(workingDirectoryPath)) {
				log.info("Working directory created - "+workingDirectoryPath);
			}
			oos.writeUTF(workingDirectoryPath.toString());
			oos.flush();
			
			String clientMachine = ClientAction.getClientName();
			log.info("Server send clientMachine - "+clientMachine);
			oos.writeUTF(clientMachine);
			oos.flush();
		}
		catch (IOException e) {
			e.getStackTrace();
		}
	}
}
