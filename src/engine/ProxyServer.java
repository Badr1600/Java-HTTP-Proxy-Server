/**
 * 
 */
package engine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Ahmed Badr
 *
 */
public class ProxyServer {

	private static final Logger LOGGER = Logger.getLogger("engine.ProxyServer");
	FileHandler serverFileHandler;

	private ServerSocket serverSocket;
	private int portNumber;

	public ProxyServer(final int portNumber, final String logFileName) {
		this.portNumber = portNumber;
		try {
			serverFileHandler = new FileHandler(logFileName);
			LOGGER.addHandler(serverFileHandler);
			SimpleFormatter formatter = new SimpleFormatter();
			serverFileHandler.setFormatter(formatter);
		} catch (SecurityException e) {
			LOGGER.severe("Security Exception: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.severe("IO Exception: " + e.getMessage());
		}
		LOGGER.info("ProxyServer instace is created with port number: " + portNumber);
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(portNumber);
			LOGGER.info("ProxyServer is intiated with port number: " + portNumber);
			int counter = 0;
			while (!serverSocket.isClosed()) {
				Socket clientSocket = serverSocket.accept();
				new ProxyClientThread(clientSocket, counter);
				counter++;
			}
		} catch (IOException e) {
			LOGGER.severe("ProxyServer I/O exception message: " + e.getMessage());
			e.printStackTrace();
		}

	}
	////////////////////////////////////////////////////////////////////////
	//
	// Basic GETTERs
	//
	////////////////////////////////////////////////////////////////////////

	/**
	 * @return the serverSocket
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	/**
	 * @return the portNumber
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	////////////////////////////////////////////////////////////////////////
	//
	// Basic FUNCTIONs
	//
	////////////////////////////////////////////////////////////////////////

}
