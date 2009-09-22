package moten.david.music.album.player;

import java.awt.Image;
import java.io.File;

public interface ImageProvider {
	Image getImage(File file, int width, int height);
}
