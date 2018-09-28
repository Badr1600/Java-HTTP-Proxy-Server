package engine.simulate;

import engine.ProxyServer;
import java.util.Scanner;

/**
 * Java Proxy Server using java sockets. HTTPS requests are not handled in this
 * server.
 * 
 * Use this “http=127.0.0.1:9999;ftp=127.0.0.1:9999;socks=127.0.0.1:9999” script
 * for setting the proxy configuartion on your machine to get the best outcome
 * form this implementation.
 * 
 * @author Ahmed Badr
 * 
 *         Title: HTTP Proxy Server
 *         Date: 27.09.2018
 *
 */

public class Start {

	private static int proxyServerPortNumber;
	private static String log_file_name_dir;

	/**
	 * Main Method Starting the Proxy Server Two parameters are passed to the server
	 * which are the Port Number of the Prxoy service and the name/directory of the
	 * log file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Handling input arguments by the user
		if (args.length == 2) {
			if (args[0] != null) {
				// make sure that the input port number has no errors
				boolean flagRequestCorrectPortNumber = parseIntegerHandleException(args[0]);
				if (!flagRequestCorrectPortNumber) {
					// receive input from the user to get a correct port number.
					Scanner receiveInputFromUser = new Scanner(System.in);
					while (!parseIntegerHandleException(receiveInputFromUser.nextLine()))
						;
					receiveInputFromUser.close();
				}
			} else {
				// receive input from the user to get a correct port number.
				Scanner receiveInputFromUser = new Scanner(System.in);
				while (!parseIntegerHandleException(receiveInputFromUser.nextLine()))
					;
				receiveInputFromUser.close();
			}
			// make sure that the input log file name has no errors
			if (args[1] != null) {
				setLog_file_name(args[1]);
			} else {
				// receive input from the user to get a correct file name.
				Scanner receiveInputFromUser = new Scanner(System.in);
				setLog_file_name(receiveInputFromUser.nextLine());
				receiveInputFromUser.close();
			}

			ProxyServer proxyserver = new ProxyServer(getProxyServerPortNumber(), getLog_file_name());
			proxyserver.start();
		} else {
			ProxyServer proxyserver = new ProxyServer(9999, System.getProperty("user.home") + "\\log");
			proxyserver.start();
		}
	}

	/**
	 * @param portNumber
	 * @param defaultValPortNumber
	 * @return
	 */
	private static boolean parseIntegerHandleException(String portNumber) {
		try {
			int tempPortNumber = Integer.parseInt(portNumber);
			if (tempPortNumber < 0) {
				NumberFormatException negativeNumberExcep = new NumberFormatException(
						"Number Format Exception, Make sure it's a positive Interger Number");
				throw negativeNumberExcep;
			} else if (tempPortNumber <= 1023) {
				NumberFormatException restrictedNumberExcep = new NumberFormatException(
						"Number Resterction Exception, Make sure port number > 1023");
				throw restrictedNumberExcep;
			} else if (tempPortNumber > 65536) {
				NumberFormatException restrictedNumberExcep = new NumberFormatException(
						"Number Resterction Exception, Make sure port number > 1023");
				throw restrictedNumberExcep;
			} else {
				setProxyServerPortNumber(tempPortNumber);
				return true;
			}
		} catch (NumberFormatException e) {
			System.out.println("Number Format Exception, Make sure it's a valid positive Interger Number");
		}
		return false;
	}

	
	////////////////////////////////////////////////////////////////////////
	//
	// Basic GETTERs
	//
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * @return the proxyServerPortNumber
	 */
	public static int getProxyServerPortNumber() {
		return proxyServerPortNumber;
	}

	/**
	 * @param proxyServerPortNumber the proxyServerPortNumber to set
	 */
	public static void setProxyServerPortNumber(int proxyServerPortNumber) {
		Start.proxyServerPortNumber = proxyServerPortNumber;
	}

	/**
	 * @return the log_file_name
	 */
	public static String getLog_file_name() {
		return log_file_name_dir;
	}

	/**
	 * @param log_file_name the log_file_name to set
	 */
	public static void setLog_file_name(String log_file_name) {
		Start.log_file_name_dir = System.getProperty("user.home") + "\\" + log_file_name;
	}
}
