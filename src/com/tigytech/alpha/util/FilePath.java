package com.tigytech.alpha.util;

import java.io.File;

public class FilePath {
	
	public static final String APP_DIR = new File("").getAbsolutePath();
	public static final String USR_TMP = System.getProperty("java.io.tmpdir");
	public static final String USR_ROOT = System.getProperty("user.home");
	public static String APP_TMP;
	
	public static boolean tempDirPresent() {
		return APP_TMP != null;
	}
	
	public static void setTempDir() {

	}
	
}