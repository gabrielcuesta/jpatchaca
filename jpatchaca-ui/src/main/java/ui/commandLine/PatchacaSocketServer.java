package ui.commandLine;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import net.PatchacaConstants;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.picocontainer.Startable;



import basic.SocketUtils;

public class PatchacaSocketServer implements Startable{


	private final CommandLineInterface cli;
	private ServerSocket serverSocket;

	public PatchacaSocketServer(CommandLineInterface cli){
		this.cli = new HttpFilterCommandLineInterface(cli);
	}
	
	@Override
	public void start() {
		
		Thread thread = new Thread(){

			@Override
			public void run() {
				try {
					int port = PatchacaConstants.SERVER_PORT;
					serverSocket = new ServerSocket(port);
					acceptConnectionsWhileUniverseExists();
				} catch (IOException e) {
					Logger.getLogger(PatchacaSocketServer.class).error("Error openind ServerSocket for PatchacaSocketServer", e);
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		
	}
	
	protected void acceptConnectionsWhileUniverseExists() throws IOException {
		final boolean universeExists = true;
		while(universeExists){
			try {
				Socket socket = serverSocket.accept();
				greet(socket);
				readCommand(socket);
			} catch (SocketException se){
				Logger.getLogger(PatchacaSocketServer.class).error(se);
				return;
			}
		}
		
	}

	private void greet(Socket socket) throws IOException {
		OutputStream output = socket.getOutputStream();
		IOUtils.write("hello from your timetracker\n", output);
		output.flush();
	}

	private void readCommand(Socket socket) throws IOException {
			try {
				final String command = SocketUtils.readLine(socket);
				if (command == null)
					return;
				SocketUtils.writeLine(cli.command(command), socket);
			} catch (SocketException ex){
				//do nothing
			} finally {
				socket.close();				
			}
	}

	@Override
	public void stop() {
		if (serverSocket == null){
			return;
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			throw new IllegalStateException("Error closing PatchacaSocketServer", e);
		}
	}

	
}
