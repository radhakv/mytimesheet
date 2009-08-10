package moten.david.util.maven.guide;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.lf5.util.StreamUtils;

public class ScalaGuideConverter {

	public void convert(InputStream is, PrintStream out) throws IOException {

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		StreamUtils.copy(is, bytes);
		String s = bytes.toString();
		InputStreamReader isr = new InputStreamReader(getClass()
				.getResourceAsStream("replacements.txt"));
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("#") && line.length() > 0) {
				String[] items = line.split("\t");
				String patternString = items[0];
				String replaceWith = "";
				if (items.length > 1)
					replaceWith = items[1];
				replaceWith = replaceWith.replaceAll("newLine", "\n");
				System.out.println("replacing " + patternString + " with "
						+ "'" + replaceWith + "'");
				Pattern pattern = Pattern.compile(patternString,
						Pattern.UNIX_LINES + Pattern.MULTILINE);
				Matcher matcher = pattern.matcher(s);
				s = matcher.replaceAll(replaceWith);
			}
		}
		out.write(s.getBytes());
		br.close();
		isr.close();
	}

	private boolean startsWithNumberDot(String line) {
		if (line.length() == 0)
			return false;
		int i = 0;
		while (i < line.length() && Character.isDigit(line.charAt(i)))
			i++;
		return (i + 1 < line.length() && line.charAt(i + 1) == '.');
	}

	private static boolean matches(String line, String regexp) {
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(line);
		return matcher.matches();
	}

	public static void main(String[] args) throws IOException {
		ScalaGuideConverter c = new ScalaGuideConverter();
		PrintStream out = new PrintStream(
				"docs/programming-in-scala-processed.txt");
		c.convert(ScalaGuideConverter.class
				.getResourceAsStream("programming-in-scala.txt"), out);

	}
}
