package com.tigytech.alpha.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Configuration {
	private final static boolean DEBUG = false; // Set to true to show debugging
												// information

	private int BUFFER_LENGTH = 1024; // Length of character used for reading
										// file
	private Hashtable<String, String> table; // Table of key value pairs

	public Configuration(File configFile) throws ConfigurationException {
		this(configFile, false, false);
	}

	public Configuration(File configFile, boolean useSysProperties,
			boolean showOverrides) throws ConfigurationException {
		// Loading a config file is a two stage process
		// The first stage scans the file and extracts key-value datums as
		// complete strings
		// The second stage iterates through these strings and separates them
		// into a key and a value

		char[] buffer = new char[BUFFER_LENGTH]; // Buffer used to read
													// characters from config
													// file
		StringBuilder configBuilder = new StringBuilder(); // Used to build up a
															// complete
															// key-value datum
		ArrayList<String> configArray = new ArrayList<String>(); // Array of
																	// key-value
																	// data,
																	// held a
																	// strings
		table = new Hashtable<String, String>(); // Table of key-value pairs

		try {
			// Create a reader for reading Config file
			InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile));

			int read; // Number of characters read on last file read
			boolean inComment = false; // Is reading currently within a comment
			boolean slash = false; // Has a slash character just been found
			boolean inString = false; // Is reading currently within a string

			// Try to read BUFFER_LENGTH characters into the buffer
			while ((read = reader.read(buffer, 0, BUFFER_LENGTH)) > 0) {
				// Iterate through the characters read
				for (int i = 0; i < read; i++) {
					// Get the current character
					char c = buffer[i];

					if (inString) {
						// If currentlny in a string, check for a closing
						// double-quote
						// If found, clear inString flag
						if (c == '"' && !slash)
							inString = false;
					} else {
						// Check for an end of line, if found make sure the
						// inComment flag is cleared
						if (c == '\n' || c == '\r') {
							inComment = false;
							continue;
						}

						// If starting a comment, or already in a comment just
						// continue
						if (c == '#' || inComment) {
							inComment = true;
							continue;
						}

						// Check for the end of a key value pair
						if (c == ';') {
							// Add the complete key-value datum to configArray
							configArray.add(configBuilder.toString());

							// Reset the configBuilder ready for the next
							// key-value datum
							configBuilder.setLength(0);
							continue;
						}

						// Check for the start of a string
						if (c == '"')
							inString = true;

						// Convert tabs into spaces, this ensures consistent
						// indentation when
						// outputting config information
						if (c == '\t')
							c = ' ';
					}

					// If current character is a slash, update flag
					slash = (c == '\\');

					// Append the current character to the configBuilder
					configBuilder.append(c);
				}
			}

			// Close the reader
			reader.close();
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}

		// Iterate through the configArray converting each line into a key value
		// pair
		for (String configLine : configArray) {
			// Get position of equals sign, if not found throw an exception
			int splitPos = configLine.indexOf('=');
			if (splitPos == -1)
				throw new ConfigurationException("Bad config line: " + configLine);

			// Extract key and value, outer whitespace is trimmed
			String key = configLine.substring(0, splitPos).trim();
			String val = configLine.substring(splitPos + 1).trim();

			// If enabled, override the loaded val with that from the System
			// Property value
			if (useSysProperties) {
				// Try to get a system property with the same key
				String sysVal = System.getProperty(key);

				// If a system property has been found, set that as the value
				if (sysVal != null) {
					val = sysVal;

					// If selected, show that the config value has been
					// overridden
					if (showOverrides)
						System.out.println("Override: " + key + " = " + val);
				}
			}

			if (DEBUG)
				System.out.println(key + " = " + val);

			// Check for key duplication
			if (table.containsKey(key)) {
				System.out.println("Config.loadFile: Duplicate keys: " + key);
			}

			// Add the key value pair to the table
			table.put(key, val);
		}
	}

	/* String Methods */
	/* -------------- */
	public String getElement(String key) {
		return table.get(key);
	}

	public String parseString(String val) throws ConfigurationException {
		// The method iterates over the characters in a string combining each
		// double-quoted section.
		// The double quotes are removed. Escaped characters are converted to
		// their true encoding
		// Within this method, string sections can only be separated by spaces
		// and tabs. During the load phase, new lines
		// charcaters will have been removed, so within the configuratio file
		// any whitespace can be
		// used to separate string sections

		int s, d; // Source and Destination character pointers
		boolean slash = false; // Has a slash character been found
		boolean inString = false; // Is reading currently within a string

		// Convert value string into array of characters
		char[] chars = val.toCharArray();

		// Iterate through string charcters
		for (s = 0, d = 0; s < chars.length; s++) {
			if (!inString) {
				// If not currently in a string, check for double-quote
				if (chars[s] == '"') {
					// Flag that now currently within a string
					inString = true;
				} else {
					// Check for invalid characters outside string
					// Only space and tabs allowed
					if (chars[s] != ' ' && chars[s] != '\t') {
						throw new ConfigurationException(
								"Illegal character between strings: '"
										+ chars[s] + "'");
					}
				}
			} else {
				if (slash == false && chars[s] == '\\') {
					// If previous character was not a slash and current
					// character
					// is a slash, flag that a slash has been found
					slash = true;
				} else {
					if (slash) {
						// If previous character was a slash, convert escaped
						// character
						// into required character
						switch (chars[s]) {
						case '\\':
							chars[d] = '\\';
							break;
						case '"':
							chars[d] = '"';
							break;
						case 'n':
							chars[d] = '\n';
							break;
						case 't':
							chars[d] = '\t';
							break;
						case 'r':
							chars[d] = '\r';
							break;
						default:
							throw new ConfigurationException("Invalid escape code: "
									+ chars[s]);
						}
						d++;
						slash = false;
					} else {
						// Check for end of string double-quote
						if (chars[s] == '"') {
							inString = false;
						} else {
							// Just copy the character from the source to
							// destination position
							chars[d] = chars[s];
							d++;
						}
					}
				}
			}
		}

		// Return the parsed string
		return new String(chars, 0, d);
	}

	public String getString(String key, String def) throws ConfigurationException {
		String val = table.get(key);
		if (val == null)
			return def;
		return parseString(val);
	}

	public String getString(String key) throws ConfigurationException {
		String val = table.get(key);
		if (val == null)
			throw new ConfigurationException("No such key - '" + key + "'");
		return parseString(val);
	}

	/* Boolean Methods */
	/* --------------- */
	public static boolean parseBoolean(String val) throws ConfigurationException {
		val = val.toLowerCase();
		if (val == "true" || val == "1")
			return true;
		if (val == "false" || val == "0")
			return false;
		throw new ConfigurationException("Invalid boolean format");
	}

	public boolean getBoolean(String key, boolean def) throws ConfigurationException {
		String val = table.get(key);
		if (val == null)
			return def;
		return parseBoolean(val);
	}

	public boolean getBoolean(String key) throws ConfigurationException {
		String val = table.get(key);
		if (val == null)
			throw new ConfigurationException("No such key - '" + key + "'");
		return parseBoolean(val);
	}

	/* Long Methods */
	/* --------------- */
	public static long parseLong(String val) throws ConfigurationException {
		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nFE) {
			throw new ConfigurationException(nFE);
		}
	}

	public long getLong(String key, int def) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			return def;
		return parseLong(val);
	}

	public long getLong(String key) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			throw new ConfigurationException("No such key - '" + key + "'");
		return parseLong(val);
	}

	/* Integer Methods */
	/* --------------- */
	public static int parseInt(String val) throws ConfigurationException {
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException nFE) {
			throw new ConfigurationException(nFE);
		}
	}

	public int getInt(String key, int def) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			return def;
		return parseInt(val);
	}

	public int getInt(String key) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			throw new ConfigurationException("No such key - '" + key + "'");
		return parseInt(val);
	}

	/* Float Methods */
	/* ------------- */
	public static float parseFloat(String val) throws ConfigurationException {
		try {
			return Float.parseFloat(val);
		} catch (NumberFormatException nFE) {
			throw new ConfigurationException(nFE);
		}
	}

	public float getFloat(String key, float def) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			return def;
		return parseFloat(val);
	}

	public float getFloat(String key) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			throw new ConfigurationException("No such key - '" + key + "'");
		return parseFloat(val);
	}

	/* Double Methods */
	/* -------------- */
	public static double parseDouble(String val) throws ConfigurationException {
		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException nFE) {
			throw new ConfigurationException(nFE);
		}
	}

	public double getDouble(String key, double def) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			return def;
		return parseDouble(val);
	}

	public double getDouble(String key) throws ConfigurationException {
		String val = getElement(key);
		if (val == null)
			throw new ConfigurationException("No such key - '" + key + "'");
		return parseDouble(val);
	}

	/* Arrays Methods */
	/* -------------- */
	public static String[] splitArray(String str) throws ConfigurationException {
		// This method takes an array encoded in a string and splits it into
		// separate strings
		// Given the support for tree structures, it splits the top level only
		// This is a two stage process
		// The first stage checks for matching pairs of brackets and find the
		// bracket depth that
		// corresponds with the top level
		// The second stage splits the array at comma positions at the top leve

		// Create an array to hold the parts of the array
		ArrayList<String> parts = new ArrayList<String>();

		// Convert string into character array
		char[] charArray = str.toCharArray();

		boolean slash = false; // Has a slash character been found
		boolean inString = false; // Is reading currently within a string

		int minCommaDepth = Integer.MAX_VALUE;
		int depth = 0;

		// Iterate through string charcters
		// Check for matching pairs of brackets
		// Find the minimum depth that contains separating commas, this
		// corresponds
		// to the highest level of the array tree
		for (int i = 0; i < charArray.length; i++) {
			if (inString) {
				if (slash && charArray[i] == '\\') {
					// If previous character was a slash and current character
					// is a slash, clear slash flag
					slash = false;
				} else if (charArray[i] == '"' && !slash) {
					// If non-escaped double-quote, no longer in string
					inString = false;
				}
			} else {
				if (charArray[i] == '"') {
					// If double-quote found, now in String
					inString = true;
				} else if (charArray[i] == '(') {
					// Open bracket signifies start of new array
					depth++;
				} else if (charArray[i] == ')') {
					// Close bracket signifies end of new array
					depth--;
				} else if (charArray[i] == ',' && depth < minCommaDepth) {
					// Record new min Depth
					minCommaDepth = depth;
				}
			}

			// If current character is a slash, update flag
			slash = (charArray[i] == '\\');
		}

		if (DEBUG)
			System.out.printf("MinDepth: %d, Final Depth: %d, Length: %d\n",
					minCommaDepth, depth, charArray.length);

		// Throw an exception if there are an unequal number of brackets
		if (depth != 0)
			throw new ConfigurationException("Badly matched array brackets");

		int startPos = 0;
		int stopPos = charArray.length;
		depth = 0;
		slash = false;
		inString = false;

		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];

			if (DEBUG)
				System.out.printf("%3d - %c %d %b\n", i, c, depth, inString);

			if (inString) {
				if (c == '\\' && slash) {
					slash = false;
				} else if (c == '"' && !slash) {
					inString = false;
				}
			} else {
				if (c == '"') {
					inString = true;
				} else if (charArray[i] == '(') {
					depth++;
					if (depth == minCommaDepth)
						startPos = i + 1;
					if (DEBUG)
						System.out.printf("( Depth: %d, StartPos: %d\n", depth,
								startPos);
				} else if (charArray[i] == ')') {
					depth--;
					if (depth == minCommaDepth - 1)
						stopPos = i;
					if (DEBUG)
						System.out.printf(") Depth: %d, StartPos: %d\n", depth,
								startPos);
				} else if (charArray[i] == ',' && depth == minCommaDepth) {
					String part = str.substring(startPos, i).trim();
					if (part.startsWith("(") && part.endsWith(")"))
						part = part.substring(1, part.length() - 1);
					if (part.length() == 0)
						throw new ConfigurationException("Empty field");
					parts.add(part);
					startPos = i + 1;

					if (DEBUG)
						System.out.println("ADDING , '" + part + "'");
				}
			}

			slash = (c == '\\');
		}

		if (DEBUG)
			System.out.printf("startPos: %d, stopPos: %d\n", startPos, stopPos);

		String part = str.substring(startPos, stopPos).trim();
		if (part.startsWith("(") && part.endsWith(")"))
			part = part.substring(1, part.length() - 1);
		if (part.length() == 0)
			throw new ConfigurationException("Empty field");
		parts.add(part);

		if (DEBUG)
			System.out.println("ADDING e '" + part + "'");

		// Convert ArrayList into array and then return
		String[] partsArray = new String[parts.size()];
		parts.toArray(partsArray);
		return partsArray;
	}

	/* Display Methods */
	/* --------------- */
	public void display(PrintStream out) {
		// Get an enumeration of all the table keys
		Enumeration<String> keys = table.keys();

		// Iterate through the keys
		while (keys.hasMoreElements()) {
			// Output the key and value
			String key = keys.nextElement();
			String val = table.get(key);
			System.out.println(key + " = " + val);
		}
	}

	/* Tree Display Methods */
	/* -------------------- */
	public static void displayTree(String str, boolean details)
			throws ConfigurationException {
		displayTree(str, 0, 0, 0, "", false,
				new TreeInfo(details, str.length()));
	}

	private static void displayTree(String str, int depth, int index,
			int parents, String indent, boolean last, TreeInfo info)
			throws ConfigurationException {
		depth++;
		String[] array = splitArray(str);

		if (array.length != 1 && parents != 0 && info.space == false) {
			System.out.println(indent + " │  ");
		}

		System.out.print(indent);

		if (depth > 1) {
			if (index == parents - 1) {
				System.out.print(" └─ ");
				indent += "    ";
				if (depth == 2 || last == true)
					last = true;
			} else {
				System.out.print(" ├─ ");
				indent += " │  ";
				last = false;
			}
		}

		if (array.length == 1)
			System.out.print(str + " ");
		else
			System.out.print(str + " ");

		if (info.details) {
			int pad = info.padSize - (indent.length() + str.length());
			for (int i = 0; i < pad; i++)
				System.out.print(".");
			System.out.printf(" Depth:%d, Index:%d, Parents:%d, Length:%d\n",
					depth, index, parents, array.length);
		} else {
			System.out.println();
		}

		info.space = false;
		if (array.length == 1 && index == parents - 1 && last == false) {
			System.out.println(indent);
			info.space = true;
		}

		if (array.length != 1) {
			for (int i = 0; i < array.length; i++) {
				displayTree(array[i], depth, i, array.length, indent, last,
						info);
			}
		}
	}

	private static class TreeInfo {
		public boolean details;
		public int padSize;
		public boolean space = true;

		public TreeInfo(boolean details, int padSize) {
			this.details = details;
			this.padSize = padSize;
		}
	}
}