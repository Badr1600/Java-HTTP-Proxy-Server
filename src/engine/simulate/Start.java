/**
 * 
 */
package engine.simulate;

import engine.ProxyServer;

/**
 * @author Ahmed Badr
 *
 */
public class Start {

	public static void main(String[] args) {
		ProxyServer proxyserver = new ProxyServer(Integer.parseInt(args[0]), args[1]);
		proxyserver.start();

//		ProxyServer proxyserver = new ProxyServer(9999, "localhost", "D:/Coding/log_server");
	}
}
