# ClashBot
Discord Bot to help manage Clash of Clans server

Hey so everything you need to get started is probably more or less here. I've left out one key file that you'll need to get this running, though:
Java class org.jprojects.lib.Token, or src/main/java/org/jprojects/lib/Token.java

It should look something like this:
package org.jprojects.lib;

import org.jprojects.lib.constants.BOTConstants;

	public class Token {
		public static String getDiscordToken() {
	    return BOTConstants.DEBUG ? // DISCORD BOT DEV TOKEN, or just null if you don't have a dev bot
	    : //DISCORD BOT TOKEN
	    ;
	  }

	  public static String getUsername() {
			return //Put your Clash of Clans Developer account email here
	    ;
		}
		public static String getPassword() {
			return  //Put your Clash of Clans Developer Account password here (I don't really like it either but eh...)
	    ;

		public static String getDBUsername() {
			return // Put your MYSQL Database Username here
	    ;
		}

		public static String getDBPassword() {
			return // Put your MYSQL Database password here
	    ;
		}
  
  /////////////////// END OF FILE ///////////////////
  
  In case you didn't notice, you'll also need a database. I used MYSQL for now. 
  Do as you please, it should be relatively easy to switch out the database for whatever flavor you prefer. 
  Changes will need to be made to the org.jprojects.lib.database package for that. 
  DatabaseConnectionPool handles connecting to the database itself, and the \*DF.java files interact with the databases. 
  Modifying these will allow you to change things like table names within the schema.
  
  You might also want to do some better security/SSL on the database connection if it's not on your closed personal network to prevent MITM attacks. :)
