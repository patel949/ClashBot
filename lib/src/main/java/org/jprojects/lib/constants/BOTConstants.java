package org.jprojects.lib.constants;

import org.jprojects.lib.Main;

public class BOTConstants {
	//Modify this value for testing/publishing.
	private static String ENVIRONMENT = BOTConstants.DEV_ENV;
	
	public static final String DEV_ENV = "DEV";
	public static final String PROD_ENV = "PROD";
	public static final boolean DEBUG = ENVIRONMENT.equals(DEV_ENV);
	
	public static final String SUBSCRIBER_RELATION = "B";
	public static final String OWNER_RELATION = "O";
	public static final String SERVER_RELATION = "V";
	
	public static final String IDENTITY_SCHEMA = "identify";
	
	public static final int SQL_OK = 0;
	public static final int SQL_FAILED_RECORD_EXISTS = 1;
	public static final int SQL_FAILED_NOT_FOUND = 2;
	public static final int SQL_FAILED_SQL_EXCEPTION = 3;
}
