package com.tigytech.alpha.util;

import com.tigytech.alpha.constants.AlphaConstants;

public class ConsoleFlags {

	public static void checkConsoleFlags(String[] args) {
		if (args == null || args.length < 0) return;
		
		int i = 0;		
		while (i < args.length && args[i].startsWith("-")) {
			String arg = args[i++];
			
			if (arg.equals("-version")) {
				System.out.println(AlphaConstants.APPLICATION_NAME + " (" + AlphaConstants.APPLICATION_VERSION + ")");
				System.exit(0);
			} else if (arg.equals("-verbose")) {
				// Initiate Verbose Flag
			} else if (arg.startsWith("-cfg-file=")) {
				String file = arg.substring(arg.indexOf('=')+1);
			} else {
				for (int j = 0; j < arg.length(); j++) {
					char flag = arg.charAt(j);
					switch (flag) {
						// Add character based flags here
					}
				}
			}
		}
		
		// Approach Used: http://www.karlin.mff.cuni.cz/network/prirucky/javatut/java/cmdLineArgs/parsing.html
	}
	
}
