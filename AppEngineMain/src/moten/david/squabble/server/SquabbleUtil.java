package moten.david.squabble.server;

import java.util.ArrayList;
import java.util.List;

public class SquabbleUtil {
	public List<String> getLetters(String language) {
		List<String> list = new ArrayList<String>();
		if ("english".equalsIgnoreCase(language)) {
			add(list, "E", 12);
			add(list, "A", 9);
			add(list, "I", 9);
			add(list, "O", 8);
			add(list, "N", 6);
			add(list, "R", 6);
			add(list, "T", 6);
			add(list, "L", 4);
			add(list, "S", 4);
			add(list, "U", 4);
			add(list, "D", 4);
			add(list, "G", 3);
			add(list, "B", 2);
			add(list, "C", 2);
			add(list, "M", 2);
			add(list, "P", 2);
			add(list, "F", 2);
			add(list, "H", 2);
			add(list, "V", 2);
			add(list, "W", 2);
			add(list, "Y", 2);
			add(list, "K", 1);
			add(list, "J", 1);
			add(list, "X", 1);
			add(list, "Q", 1);
			add(list, "Z", 1);
		} else if ("spanish".equalsIgnoreCase(language)) {

		} else
			throw new RuntimeException("unrecognized language: " + language);
		return list;
	}

	private void add(List<String> list, String s, int number) {
		for (int i = 0; i < number; i++)
			list.add(s);
	}
}
