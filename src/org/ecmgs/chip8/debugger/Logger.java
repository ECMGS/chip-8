package org.ecmgs.chip8.debugger;

public class Logger {
	
	/**
	 * Logs the desired information
	 * 
	 * @param s (String) a string containing the message to be logged
	 */
	public static void log(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * Logs the desired information as an error message
	 * 
	 * @param s (String) error message to be logged
	 */
	public static void err(String err) {
		System.err.println(err);
	}
	
}
