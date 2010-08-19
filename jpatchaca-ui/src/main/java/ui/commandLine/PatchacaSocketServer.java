package ui.commandLine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.picocontainer.Startable;


import basic.SocketUtils;

public class PatchacaSocketServer implements Startable{


	private final CommandLineInterface cli;
	private ServerSocket serverSocket;

	public PatchacaSocketServer(CommandLineInterface cli){
		this.cli = cli;
	}
	
	@Override
	public void start() {
		
		Thread thread = new Thread(){

			@Override
			public void run() {
				try {
					int port = 48625;
					serverSocket = new ServerSocket(port);
					acceptConnectionsWhileUniverseExists();
				} catch (IOException e) {
					throw new RuntimeException("Error openind ServerSocket for PatchacaSocketServer", e);
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		
	}
	
	protected void acceptConnectionsWhileUniverseExists() throws IOException {
		final boolean universeExists = true;
		while(universeExists){
			Socket socket = serverSocket.accept();
			readCommandsWhileUniverseExists(socket);
		}
		
	}

	private void readCommandsWhileUniverseExists(Socket socket) throws IOException {
		final boolean universeExists = true;
		while(universeExists){
			try {
				final String command = SocketUtils.readLine(socket);
				if (command == null)
					return;
				SocketUtils.writeLine(cli.command(command), socket);				
			} catch (SocketException ex){
				//do nothing
			}
		}
	}

	@Override
	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new IllegalStateException("Error closing PatchacaSocketServer", e);
		}
	}

	
}
