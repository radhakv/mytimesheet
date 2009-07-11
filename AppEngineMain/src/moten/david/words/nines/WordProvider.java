package moten.david.words.nines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WordProvider {

	private List<String> words = new ArrayList<String>();

	public WordProvider() {
		InputStream is = getClass().getResourceAsStream("/nines-normal.txt");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		try {
			while ((line = br.readLine()) != null) {
				words.add(line.trim());
			}
			br.close();
			isr.close();
			is.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String nextRandomWord() {
		int index = (int) Math.round(Math.floor(Math.random() * words.size()));
		return words.get(index);
	}

	private static class Pair {
		long freq;
		String word;

		@Override
		public String toString() {
			return word + " " + freq;
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
				"src/ukwords.txt"));
		String line;
		List<Pair> list = new ArrayList<Pair>();
		while ((line = reader.readLine()) != null) {
			String[] items = line.split(" ");
			Pair pair = new Pair();
			pair.freq = new Long(items[1]);
			pair.word = items[0];
			list.add(pair);
		}
		Collections.sort(list, new Comparator<Pair>() {

			@Override
			public int compare(Pair o1, Pair o2) {

				if (o1.freq == o2.freq)
					return o1.word.compareTo(o2.word);
				else
					return (int) (o1.freq - o2.freq);
			}
		});
		for (Pair pair : list) {
			if (pair.word.length() == 9
					&& (!pair.word.contains("-") && (!pair.word.endsWith("s") || pair.word
							.endsWith("ss"))))
				System.out.println(pair.word.toUpperCase());
		}

	}
}
