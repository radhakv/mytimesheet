package moten.david.music.album.player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MusicFolder {
	private final File image;
	private final String artist;
	private final String title;
	private final String comment;
	private final File directory;

	public File getImage() {
		return image;
	}

	public MusicFolder(File image, String artist, String title, String comment,
			File directory) {
		super();
		this.image = image;
		this.artist = artist;
		this.title = title;
		this.comment = comment;
		this.directory = directory;
	}

	public String getArtist() {
		return artist;
	}

	public String getTitle() {
		return title;
	}

	public String getComment() {
		return comment;
	}

	public File getDirectory() {
		return directory;
	}

	public void saveImageToDirectory(String address) {
		try {
			URL url = new URL(address);
			InputStream is = url.openStream();
			FileOutputStream fos = new FileOutputStream(new File(directory,
					"album-player.jpg"));
			byte[] bytes = new byte[4096];
			int n = 0;
			while ((n = is.read(bytes)) > 0) {
				fos.write(bytes, 0, n);
			}
			fos.close();
			is.close();
			System.out.println("saved " + address);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
