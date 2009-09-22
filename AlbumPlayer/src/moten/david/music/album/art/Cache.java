package moten.david.music.album.art;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Cache {

	public static String getFileUrl(String url) {
		if (url.startsWith("file://"))
			return url;
		File cache = new File(System.getProperty("user.home")
				+ "/.AlbumPlayer/cache/");
		if (!cache.exists() && !cache.mkdirs())
			throw new RuntimeException("could not create cache " + cache);
		String filename = new String(Base64UrlSafe.encodeBase64(url.getBytes()));
		File file = new File(cache, filename + ".jpg");
		if (!file.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				InputStream is = new URL(url).openStream();
				byte[] bytes = new byte[4096];
				int n;
				while ((n = is.read(bytes)) != -1)
					fos.write(bytes, 0, n);
				fos.close();
				is.close();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			System.out.println(file + " exists");
		}
		return file.toURI().toString();
	}

}
