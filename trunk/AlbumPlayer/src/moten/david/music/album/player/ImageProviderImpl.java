package moten.david.music.album.player;

import java.awt.Image;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;

public class ImageProviderImpl implements ImageProvider {

	private final ConcurrentHashMap<File, Image> map = new ConcurrentHashMap<File, Image>();

	@Override
	public Image getImage(File file, int width, int height) {
		synchronized (file) {
			if (map.get(file) == null) {
				Image image = new ImageIcon(file.getAbsolutePath()).getImage()
						.getScaledInstance(width, height, Image.SCALE_FAST);
				map.put(file, image);
			}
			return map.get(file);
		}
	}

}
