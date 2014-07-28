package com.tigytech.alpha.util;


import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * This class does a simple HTTP GET and writes the retrieved content to a local
 * file
 * 
 * @author Brian Pipa - http://pipasoft.com
 * @version 1.0
 */
public class WGET {
	
	public static void get(String url, String filename) throws Exception {
		String cmd = "wget -O " + filename + " " + url;
		Process process = Runtime.getRuntime().exec(cmd);
		
		BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String data = "";
		while ((data = r.readLine()) != null) System.out.println(data + "\n");
		
		process.waitFor();
	}
}