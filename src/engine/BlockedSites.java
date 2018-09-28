package engine;

/**
 * @author Ahmed Badr
 * 
 *         Title: HTTP Proxy Server 
 *         Date: 24.09.2018
 *
 */

public enum BlockedSites {

	A_BADR			("a-badr.com"), 
	GUC				("guc.edu.eg"), 
	FB				("facebook.com");

	// --- Access Methods ---------------------
	private String value;

	/**
	 * @param BlockedSite URL
	 */
	private BlockedSites(String value) {
		this.value = value;
	}

	/**
	 * @return Blocked site string value.
	 */
	public String getValue() {
		return this.value;
	}
}
