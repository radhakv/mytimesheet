package moten.david.music.album.player;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import moten.david.music.album.art.AlbumArt;

public class AlbumCover extends JPanel {

	private static final long serialVersionUID = -5595541142206859633L;

	private final List<AlbumCoverListener> listeners = new ArrayList<AlbumCoverListener>();

	private final AlbumArt albumArt;

	public AlbumCover(MusicFolder musicFolder, ImageProvider imageProvider,
			int iconWidth, int iconHeight, AlbumArt albumArt) {

		this.albumArt = albumArt;
		setLayout(new GridLayout(1, 1));
		String fileInfo = "";
		if (musicFolder.getImage() != null)
			fileInfo = musicFolder.getImage().getAbsolutePath() + ","
					+ musicFolder.getImage().length() / 1000 + "KB";
		String captionHtml = "<html><p>" + musicFolder.getArtist() + "</p><p>"
				+ musicFolder.getTitle() + "</p>" + "<p>" + fileInfo
				+ "</p><html>";
		MouseListener mouseListener = createMouseListener(musicFolder);
		if (musicFolder.getImage() != null) {
			Image img = imageProvider.getImage(musicFolder.getImage(),
					iconWidth, iconHeight);
			JLabel label = new JLabel(new javax.swing.ImageIcon(img));
			addMouseListener(mouseListener);
			add(label);
		} else {
			JLabel label = new JLabel(captionHtml);
			add(label);
			setPreferredSize(new Dimension(iconWidth, iconHeight));
			addMouseListener(mouseListener);
		}
		setToolTipText(captionHtml);
	}

	public void addListener(AlbumCoverListener l) {
		listeners.add(l);
	}

	public boolean removeListener(AlbumCoverListener l) {
		return listeners.remove(l);
	}

	private MouseListener createMouseListener(final MusicFolder musicFolder) {
		return new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					firePlay(musicFolder);
				}
				if (e.getButton() == MouseEvent.BUTTON2) {
					AlbumArtFinder finder = new AlbumArtFinder(albumArt,
							musicFolder);
					finder.findCoverArt();
				}
			}
		};
	}

	protected void firePlay(MusicFolder musicFolder) {
		for (AlbumCoverListener l : listeners)
			l.play(musicFolder);
	}

}
