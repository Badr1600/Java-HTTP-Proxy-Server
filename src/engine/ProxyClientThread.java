/**
 * 
 */
package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import engine.Expression;

/**
 * @author Ahmed Badr
 *
 */
public class ProxyClientThread extends Thread {

	public static final Logger LOGGER_CLINETS = Logger.getLogger("engine.ProxyClientThread");

	private Socket clientsSocket;
	private BufferedReader inFromClient;
	private String requestBody;
	private OutputStream outToClientTemp;
	private int threadNumber;

	public ProxyClientThread(final Socket socket, int threadNumber) {
		this.requestBody = new String();
		this.clientsSocket = socket;
		this.threadNumber = threadNumber;
		this.start();
	}

	////////////////////////////////////////////////////////////////////////
	//
	// Basic GETTERs
	//
	////////////////////////////////////////////////////////////////////////

	/**
	 * @return the clientsSocket
	 */
	public Socket getClientsSocket() {
		return clientsSocket;
	}

	/**
	 * @return the inFromClient
	 */
	public BufferedReader getInFromClient() {
		return inFromClient;
	}

	/**
	 * @return the threadNumber
	 */
	public int getThreadNumber() {
		return threadNumber;
	}

	@Override
	public void run() {
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientsSocket.getInputStream()));
			outToClientTemp = clientsSocket.getOutputStream();
			LOGGER_CLINETS.info("New ProxyClientThread instace created & Run Method Called." + "\n"
					+ "I/O streams have been created successfully.");
			String receivedWord;
			String hostName = new String();
			int portNumber = 80;
			while ((receivedWord = inFromClient.readLine()) != null) {
				if (!receivedWord.equals("")) {
					if (receivedWord.contains(Expression.CONNECT.expressionName())) {
						Pattern CONNECT_PATTERN = Pattern.compile("CONNECT (.+):(.+) HTTP/(1\\.[01])",
								Pattern.CASE_INSENSITIVE);
						Matcher matcher = CONNECT_PATTERN.matcher(receivedWord);
						if (matcher.matches()) {
							String sslHostName = matcher.group(1);
							int sslPortNumber = Integer.parseInt(matcher.group(2));
							sendHTTPRequest(sslHostName, requestBody, sslPortNumber);
						}
					} else if (receivedWord.startsWith(Expression.GET_REQUEST.expressionName())) {
						requestBody = requestBody + receivedWord + "\n";
					} else if (receivedWord.startsWith(Expression.HOST.expressionName())) {
						Pattern CONNECT_PATTERN = Pattern.compile("Host: (.+):(.+)", Pattern.CASE_INSENSITIVE);
						Matcher matcher = CONNECT_PATTERN.matcher(receivedWord);
						if (matcher.matches()) {
							hostName = matcher.group(1);
							portNumber = Integer.parseInt(matcher.group(2));
						} else {
							Pattern CONNECT_PATTERN_2 = Pattern.compile("Host: (.+)", Pattern.CASE_INSENSITIVE);
							Matcher matcher_2 = CONNECT_PATTERN_2.matcher(receivedWord);
							if (matcher_2.matches()) {
								hostName = matcher_2.group(1);
							}
							if (isBlockedSite(hostName)) {
								LOGGER_CLINETS.info("Blocked Site");
								Date date = new Date();
								long time = date.getTime();
								Timestamp timeStamp = new Timestamp(time);
								ProxyServer.getLogger()
										.info("<" + hostName + ">" + "<"
												+ clientsSocket.getInetAddress().getHostAddress() + ">" + "<"
												+ timeStamp + ">" + "<NotAllowed>");
								httpResponseBlockedSite();
								break;
							}
							requestBody = requestBody + receivedWord + "\n";
						}
					} else if (receivedWord.startsWith(Expression.USER_AGET.expressionName())) {
						requestBody = requestBody + receivedWord + "\n";
					} else if (receivedWord.startsWith(Expression.ACCEPT.expressionName())) {
						requestBody = requestBody + receivedWord + "\n";
					} else if (receivedWord.startsWith(Expression.ACCEPT_ENCODING.expressionName())) {
						requestBody = requestBody + receivedWord + "\n";
					}
				} else {
					requestBody = requestBody + "\n";
					if (portNumber == 80)
						sendHTTPRequest(hostName, requestBody, portNumber);
					requestBody = "";
				}
			}
		} catch (IOException e) {
			LOGGER_CLINETS.severe("ProxyClientThread O exception message: " + e.getMessage());
		}
	}

	/**
	 * Send HTTP request through the PROXY server the methods takes the host name/IP
	 * address of the correspondent server to GET the HTTP request and the port
	 * number, in addition to the body of the HTTP request which contains
	 * information of the required files to download for the server.
	 * 
	 * @param hostname
	 * @param requestBody
	 * @param portNumber
	 */
	private synchronized void sendHTTPRequest(String hostname, final String requestBody, final int portNumber) {
		try {
			Socket internetServer = new Socket(hostname, portNumber);
			// To get the time stamp needed for logging purposes
			Date date = new Date();
			long time = date.getTime();
			Timestamp timeStamp = new Timestamp(time);
			// Adding log entry to the log file
			ProxyServer.getLogger().info("<" + hostname + ">" + "<" + clientsSocket.getInetAddress().getHostAddress()
					+ ">" + "<" + timeStamp + ">" + "<Allowed>");
			// Initializing the channel stream to send the HTTP request to the Internet
			// server.
			PrintWriter outToInternetServer = new PrintWriter(new OutputStreamWriter(internetServer.getOutputStream()));
			// Initialize a thread to handle the HTTP request to send and to off load work
			// of
			// the main thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					// send the HTTP request.
					outToInternetServer.println(requestBody);
					// Make sure all data are sent the server.
					outToInternetServer.flush();
					LOGGER_CLINETS
							.info("------------------- Received & Starting HTTP Request Connection -------------------"
									+ "\n" + "HostName: " + hostname + "\n" + "PortNumber: " + portNumber + "\n"
									+ "RequestBody: " + "\n" + requestBody + "\n"
									+ "------------------- HTTP Request Sent Successfuly -------------------" + "\n");
					try {
						// Start to interpret the coming response from the Internet server by saving the
						// incoming byte stream and forwarding it to the web browser socket.
						InputStream inFromIntenetServer = internetServer.getInputStream();
						byte[] buffer = new byte[8192];
						int read;
						// loop for forwarding all the received data to the web browser of the clinet
						// until all data are sent then close all open channel to save memory.
						do {
							read = inFromIntenetServer.read(buffer);
							if (read > 0) {
								outToClientTemp.write(buffer, 0, read);
							}
							if (inFromIntenetServer.available() < 1) {
								outToClientTemp.flush();
							}
						} while (read >= 0);
						outToInternetServer.close();
						inFromIntenetServer.close();
						internetServer.close();
						LOGGER_CLINETS.info("------------------- Response Sent to Client -------------------");
					} catch (IOException e1) {
						LOGGER_CLINETS.severe("IO Exception: " + e1.getMessage());
					}
				}
			}).start();
		} catch (IOException e) {
			LOGGER_CLINETS.severe("IO Exception: " + e.getMessage());
		}
	}

	/**
	 * Construct the Response Header in case of blocked web site scenario.
	 */
	private synchronized void httpResponseBlockedSite() {
		new Thread(new Runnable() {

			String responseBody = "HTTP/1.1 403 Access Forbidden\r\n\n"
					+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n"
					+ "<HTML><HEAD>\r\n" + "<TITLE>403 Access Forbidden</TITLE>\r\n" + "</HEAD><BODY>\r\n"
					+ "<H1>403 Access Forbidden</H1>\r\n" + "<H5>Contact System Admin!</H5>\r\n" + "</BODY></HTML>"
					+ "\r\n\n";

			@Override
			public void run() {
				try {
					PrintWriter outToClient = new PrintWriter(new OutputStreamWriter(clientsSocket.getOutputStream()));
					outToClient.println(responseBody);
					outToClient.flush();
					outToClient.close();
				} catch (IOException e) {
					LOGGER_CLINETS.severe("IO Exception: " + e.getMessage());
				}
			}
		}).start();
	}

	/**
	 * Search for the stored blocked Web Sites in the Proxy server.
	 * 
	 * @param website
	 * @return {@link Boolean} true if the website exsists in the blocked sites
	 *         databases. Otherwise false.
	 */
	private boolean isBlockedSite(String website) {
		BlockedSites[] blockedSites = BlockedSites.values();
		for (int i = 0; i < blockedSites.length; i++) {
			if (website.contains(blockedSites[i].getValue())) {
				return true;
			}
		}
		return false;
	}
}
