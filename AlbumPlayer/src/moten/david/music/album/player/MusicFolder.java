package moten.david.music.album.player;

import java.io.File;

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
}
