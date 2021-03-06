package com.berzenin.backup.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lombok.extern.java.Log;

@Log
public class JFrameForArgs {

	private Path workingDirectoryPath;
	private int portForServer;

	public void createGUI() {
		JFrame f = new JFrame("Backup service - server");
		f.setLocation(450, 250);
		JLabel lab = new JLabel("Please, enter parametrs for connect with clients");
		lab.setBounds(10, 10, 300, 30);
		JTextField workingDirectory = new JTextField("C:\\server");
		workingDirectory.setBounds(10, 40, 230, 30);
		JButton dir = new JButton("Select...");		
		dir.setBounds(260, 40, 100, 30);
		JTextField port = new JTextField("3345");
		port.setBounds(10, 80, 230, 30);
		JButton b = new JButton("Submit");
		b.setBounds(100, 120, 100, 40);
		JLabel label1 = new JLabel();
		label1.setBounds(10, 110, 200, 100);
		f.add(lab);
		f.add(label1);
		f.add(workingDirectory);
		f.add(dir);
		f.add(port);
		f.add(b);
		f.setSize(400, 250);
		f.setLayout(null);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					workingDirectoryPath = Paths.get(workingDirectory.getText());
					portForServer = Integer.parseInt(port.getText());
					log.info(workingDirectoryPath + " " + portForServer);
					label1.setText("Args has been submitted.");
					f.dispose();
					new App(workingDirectoryPath, portForServer);
				} catch (NumberFormatException e) {
					label1.setText("Args have error values.");
				}
			}
		});
		dir.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				workingDirectory.setText(directoryChosen());				
			}
		});
	}
	
	private String directoryChosen() {
	    JFileChooser chooser = new JFileChooser();
	    try {
	    chooser.setCurrentDirectory(new File("."));
	    chooser.setDialogTitle("Select directory");
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setAcceptAllFileFilterUsed(false);
		    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			      System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
			    } else {
			      System.out.println("No Selection ");
			    }
			    log.info(chooser.getSelectedFile().toString());
			    return chooser.getSelectedFile().toString();
	    } catch (NullPointerException e) {
	    	
	    }
	    return "none";
	}
}
