package moten.david.util.maven.guide;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Converter {

	public void convert(InputStream is, PrintStream out) throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String previousLine = null;
		String line;
		while ((line = br.readLine()) != null) {
			if (previousLine != null) {
				boolean retainNewLine = line.startsWith(" ")
						|| previousLine.startsWith(" ")
						|| startsWithNumberDot(line)
						|| startsWithNumberDot(previousLine)
						|| previousLine.endsWith(".")
						|| previousLine.startsWith("Chapter ")
						|| line.startsWith("Chapter ")
						|| previousLine.startsWith("Example ")
						|| line.startsWith("Example ")
						|| previousLine.startsWith("--")
						|| line.startsWith("--") || previousLine.endsWith(":")
						|| previousLine.startsWith("==")
						|| line.startsWith("==")
						|| previousLine.startsWith("[") || line.startsWith("[")
						|| line.length() == 0 || previousLine.startsWith("A.")
						|| line.startsWith("A.")
						|| previousLine.startsWith("B.")
						|| line.startsWith("B.");
				if (retainNewLine)
					out.println(previousLine);
				else
					out.print(previousLine + " ");
			}
			previousLine = line;
		}
		out.println(line);
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

	public static void main(String[] args) throws IOException {
		Converter c = new Converter();
		PrintStream out = new PrintStream(new File(
				"docs/maven-guide-processed.txt"));
		c.convert(Converter.class.getResourceAsStream("maven-guide.txt"), out);
	}

}
