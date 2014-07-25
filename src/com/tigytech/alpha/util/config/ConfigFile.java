package com.tigytech.alpha.util.config;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ConfigFile {

	public static void loadFile(URL configLocation) {
		try {
			List<String> configData = FileUtils.readLines(FileUtils.toFile(configLocation));
			for (String s : configData) {
				
			}
		} catch (IOException e) {
			System.err.println("Unable to read the configuration file.");
			e.printStackTrace();
		}
	}
	
}