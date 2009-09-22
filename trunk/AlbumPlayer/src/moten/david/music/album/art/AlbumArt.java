package moten.david.music.album.art;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class AlbumArt {

	private static final String baseUrl = "http://www.albumart.org/index.php?srchkey=${keywords}&itempage=1&newsearch=1&searchindex=Music";

	public List<String> searchForImageUrls(String artist, String album) {
		List<String> keywords = getWords(artist);
		keywords.addAll(getWords(album));
		StringBuffer s = new StringBuffer();
		for (String keyword : keywords) {
			if (s.length() > 0)
				s.append("+");
			s.append(keyword);
		}
		String address = baseUrl.replace("${keywords}", s.toString());
		try {
			URL url = new URL(address);
			InputStream is = url.openStream();
			List<String> imageUrls = getImageUrls(is);
			System.out.println(imageUrls);
			return imageUrls;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private List<String> getImageUrls(InputStream is) throws IOException {
		String html = getString(is);
		Pattern pattern = Pattern
				.compile("http://ecx(/|\\.|\\d|[a-z]|[A-Z]|-)*\\.jpg");
		Matcher matcher = pattern.matcher(html);
		ArrayList<String> list = new ArrayList<String>();
		while (matcher.find())
			list.add(matcher.group());
		return list;
	}

	private String getString(InputStream is) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = is.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	private List<String> getWords(String words) {
		String[] items = words.split(" ");
		return new ArrayList<String>(Arrays.asList(items));
	}

	public static void main(String[] args) throws IOException {
		System.setProperty("http.proxyHost", "proxy.amsa.gov.au");
		System.setProperty("http.proxyPort", "8080");
		AlbumArt art = new AlbumArt();
		List<String> list = art.searchForImageUrls("David Bowie", "Lodger");
		StringBuffer html = new StringBuffer("<html>");
		List<String> urls = art.getImageUrls(art.getClass()
				.getResourceAsStream("test.html"));
		for (String s : list) {
			html.append("<img src=\"" + s + "\"></img>");
		}
		html.append("</html>");

		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane()
				.add(new JScrollPane(new JLabel(html.toString())));
		frame.setVisible(true);
	}
}
