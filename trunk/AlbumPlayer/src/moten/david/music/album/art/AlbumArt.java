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

	public List<UrlPair> searchForImageUrls(String artist, String album) {
		List<String> keywords = getWords(artist);
		keywords.addAll(getWords(album));
		List<UrlPair> list = getUrls(keywords);
		list.addAll(getUrls(getWords(album)));
		list.addAll(getUrls(getWords(artist)));
		return list;
	}

	private List<UrlPair> getUrls(List<String> keywords) {
		if (keywords.size() == 0)
			return new ArrayList<UrlPair>();
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
			List<UrlPair> imageUrls = getImageUrls(is);
			System.out.println(imageUrls);
			return imageUrls;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class UrlPair {
		private final String small;
		private final String large;

		public UrlPair(String small, String large) {
			super();
			this.small = small;
			this.large = large;
		}

		public String getSmall() {
			return small;
		}

		public String getLarge() {
			return large;
		}

	}

	private List<UrlPair> getImageUrls(InputStream is) throws IOException {
		String html = getString(is);
		Pattern pattern = Pattern
				.compile("http://ecx(/|\\.|\\d|[a-z]|[A-Z]|-)*\\._SL160_\\.jpg");
		Matcher matcher = pattern.matcher(html);
		ArrayList<UrlPair> list = new ArrayList<UrlPair>();
		while (matcher.find())
			list.add(new UrlPair(matcher.group(), matcher.group().replace(
					"._SL160_", "")));
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
		if (words == null || words.trim().length() == 0)
			return new ArrayList<String>();
		String[] items = words.split(" ");
		return new ArrayList<String>(Arrays.asList(items));
	}

	public static void main(String[] args) throws IOException {
		// System.setProperty("http.proxyHost", "proxy.amsa.gov.au");
		// System.setProperty("http.proxyPort", "8080");
		AlbumArt art = new AlbumArt();
		List<UrlPair> list = art.searchForImageUrls("David Bowie", "Lodger");
		StringBuffer html = new StringBuffer("<html>");
		// List<String> urls = art.getImageUrls(art.getClass()
		// .getResourceAsStream("test.html"));
		for (UrlPair s : list) {
			html.append("<img src=\"" + s.getSmall() + "\"></img>");
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
