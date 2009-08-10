package moten.david.util.maven.guide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String args[]) {
		String regex = "(\\w)(\\d)(\\w+)";

		Pattern pattern = Pattern.compile(regex);

		String candidate = "X99 ";

		Matcher matcher = pattern.matcher(candidate);

		String tmp = matcher.replaceAll("$33");

		System.out.println("REPLACEMENT: " + tmp);
		System.out.println("ORIGINAL: " + candidate);
	}
}