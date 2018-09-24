/**
 * 
 */
package engine;

/**
 * @author Ahmed Badr
 *
 */
public enum Expression {

	GET_REQUEST				("GET "), 
	POST_REQUEST			("POST "), 
	CONNECT					("CONNECT"), 
	HOST					("Host: "), 
	USER_AGET				("User-Agent: "),
	INVALID_REQUEST			("Invalid-Request"), 
	ACCEPT					("Accept:"), 
	ACCEPT_ENCODING			("Accept-Encoding:"), 
	BLANK_LINE				(" ");

	// --- Access Methods ---------------------
	private String name;

	/**
	 * @param requestFiled.
	 */
	private Expression(String name) {
		this.name = name;
	}

	/**
	 * @return the request filed value as string.
	 */
	public String expressionName() {
		return name;
	}

}
