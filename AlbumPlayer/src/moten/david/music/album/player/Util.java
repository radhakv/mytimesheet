package moten.david.music.album.player;

import java.io.File;

public class Util {

	public static boolean isAudio(File f) {
		return (f.getName().toUpperCase().endsWith(".WMA")
				|| f.getName().toUpperCase().endsWith(".MP3") || f.getName()
				.toUpperCase().endsWith(".OGG"));

	}
}
