package moten.david.kv.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.lf5.util.StreamUtils;
import org.junit.Test;

public class TestPost {
	private static final String UTF_8 = "UTF-8";
	private static final String CONTENT_TYPE_ENCODED_KMZ = "application%2Fvnd.google-earth.kmz";

	private String encode(String s) {
		try {
			return URLEncoder.encode(s, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testPost() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		StreamUtils.copy(getClass().getResourceAsStream("example-short.kml"),
				bytes);
		System.out.println(bytes.toString());
		postKeyValue("amsaCraftpic", bytes.toString(), false);
	}

	@Test
	public void testPostLarge() throws IOException {
		InputStream is = getClass().getResourceAsStream("example.kml.zip");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		StreamUtils.copy(is, bytes);
		System.out.println("bytes size=" + bytes.size());
		String value = Base64.toString(bytes.toByteArray());
		System.out.println("b64 size=" + value.length());
		postKeyValue("amsaCraftpic", value);
	}

	private void postKeyValue(String key, String value) {
		boolean append = false;
		StringBuffer s = new StringBuffer(value);
		while (s.length() > 0) {
			String val;
			int MAX_PART_SIZE = 80000;
			if (s.length() > MAX_PART_SIZE) {
				val = s.substring(0, MAX_PART_SIZE);
				s.delete(0, MAX_PART_SIZE);
			} else {
				val = s.toString();
				s = new StringBuffer();
			}
			System.out.println("sending=\n" + val.length());
			postKeyValue(key, val, append);
			append = true;
		}
	}

	private void postKeyValue(String key, String value, boolean append) {
		try {
			URL url = new URL("http://win-win.appspot.com/kv");
			URLConnection urlConn = url.openConnection();
			// URL connection channel.
			urlConn = url.openConnection();
			// Let the run-time system (RTS) know that we want input.
			urlConn.setDoInput(true);
			// Let the RTS know that we want to do output.
			urlConn.setDoOutput(true);
			// No caching, we want the real thing.
			urlConn.setUseCaches(false);
			// Specify the content type.
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// Send POST output.
			OutputStreamWriter out = new OutputStreamWriter(urlConn
					.getOutputStream());
			final String eq = "=";
			final String and = "&";
			final String encodedValue = encode(value);
			System.out.println("encValue size=" + encodedValue.length());
			String content = encode("action") + eq
					+ encode((append ? "append" : "put")) + and + encode("key")
					+ eq + encode(key) + and + encode("value") + eq
					+ encodedValue;
			out.write(content);
			out.flush();
			// Get response data.
			BufferedReader br = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			String str;
			while (null != ((str = br.readLine()))) {
				System.out.println(str);
			}
			br.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
