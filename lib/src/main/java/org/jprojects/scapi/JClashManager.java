package org.jprojects.scapi;

import org.jprojects.lib.Token;

import Core.JClash;

public class JClashManager {
private static JClashManager singleton = new JClashManager();
	
	private static JClash clash = null;
	
	public static JClash getJClash() {
		if (clash == null) {
			try {
				clash = new JClash(Token.getUsername(), Token.getPassword());
			} catch (Exception e) {
				System.err.println("Fatal error: JClash failed to initialize.");
				e.printStackTrace();
				System.exit(-1);
			}
			System.out.println("JClash initialized successfully.");
		}
		return clash;
			
	}
	
	
}
