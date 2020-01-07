/**
 * 
 */
/**
 * @author Birk
 *
 */
module pixit {
	
	exports net.bmagnu.pixit.common;
	
	exports net.bmagnu.pixit.server.handlers;
	
	exports net.bmagnu.pixit.server;
	
	exports net.bmagnu.pixit.client;
	
	exports net.bmagnu.pixit.client.handlers;

	requires javafx.controls;
	
	requires javafx.fxml;
	
	requires transitive javafx.graphics;
	
	requires javafx.base;
	
	requires transitive com.google.gson;
	
	opens net.bmagnu.pixit.client to javafx.fxml;
}