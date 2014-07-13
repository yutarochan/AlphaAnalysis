package com.tigytech.alpha;

import java.util.ArrayList;

public class FileReader {

	public static void main(String[] args) {
		// Sentence does not work with Mr. --> creates a separate line...
		String sentence = "RIO DE JANEIRO — For years, Brazilians had a phrase they would inevitably utter when things went wrong. “Imagina na Copa,” they told one another after an endless traffic jam or a construction accident or an ugly rash of violence dominated the news — imagine if this happened during the World Cup. It was a foreboding warning, a pre-emptive sigh of the presumed disasters that lay ahead. Over five weeks, though, Brazil avoided any of the major disasters it feared as dramatic games and entertaining soccer — as well as the national team’s own stunning collapse — generally overshadowed any logistical issues. It was fitting, then, that in the final game here, Brazilians narrowly dodged the ultimate on-field nightmare, too. Mr. Ong wants some swag.";
		ArrayList<String> s = splitText(sentence);
		for (String sent : s)
			System.out.println(sent);
	}
	
	private static ArrayList<String> splitText(String text) {
		ArrayList<String> sentences = new ArrayList();
		int searchIndex = 0;
		while (true) {
			int index = -1;
			int quoteIndex = text.indexOf('"');
			int periodIndex = text.indexOf('.');
			int questionIndex = text.indexOf('?');
			
			if (periodIndex >= 0) index = periodIndex;
			if ((questionIndex >= 0) && ((periodIndex < 0) || (questionIndex < periodIndex))) index = questionIndex;
			if (index < 0) break;
			if ((quoteIndex >= 0) && (quoteIndex < index))
				searchIndex = text.indexOf('"', quoteIndex + 1) + 1;
			else {
				String sentence = text.substring(0, index + 1).trim();
				sentences.add(sentence);
				text = text.substring(index + 1);
				searchIndex = 0;
			}
		}
		
		return sentences;
	}
}
