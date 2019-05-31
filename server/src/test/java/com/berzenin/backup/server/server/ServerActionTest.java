package com.berzenin.backup.server.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.berzenin.backup.server.ServerAction;

public class ServerActionTest {

	
	@Test
	void setWorkingDirectoryTest () throws IOException {
		Path server = Paths.get("c:\\backup");
		String clientAMchineName = "fil";
		Path clientDirectory = Paths.get("c:\\client\\dir");
		
		
    	assertEquals(Paths.get("c:\\backup\\fil\\client\\dir"), 
    			ServerAction.setWorkingDirectory(
    					clientDirectory, 
    					clientAMchineName, 
    					server));
	}
	
	@Test
	void sendInformationAboutWorkDirectoryStateTest () throws IOException {
		Path directory = Paths.get("C:\\server\\Home\\client\\dir2");
		ServerAction.getInformationAboutWorkDirectoryState(directory);
		assertEquals(true, true);
	}
}
