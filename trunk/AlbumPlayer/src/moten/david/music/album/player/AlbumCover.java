package moten.david.music.album.player;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import moten.david.music.album.art.AlbumArt;
import moten.david.music.album.player.event.ClearFilter;
import moten.david.music.album.player.event.EditFilter;

public class AlbumCover extends JPanel {

	private static final long serialVersionUID = -5595541142206859633L;

	private final List<AlbumCoverListener> listeners = new ArrayList<AlbumCoverListener>();

	private final AlbumArt albumArt;

	private final JPopupMenu popup;

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
		popup = new JPopupMenu();
		{
			JMenuItem menuItem = new JMenuItem("Play");
			ActionListener play = createPlayActionListener(musicFolder);
			menuItem.addActionListener(play);
			popup.add(menuItem);
		}
		{
			JMenuItem menuItem = new JMenuItem("Find artwork");
			ActionListener play = createFindArtworkActionListener(musicFolder);
			menuItem.addActionListener(play);
			popup.add(menuItem);
		}
		{
			JMenuItem menuItem = new JMenuItem("Explore");
			ActionListener play = createExploreListener(musicFolder);
			menuItem.addActionListener(play);
			popup.add(menuItem);
		}
		{
			popup.addSeparator();
			JMenuItem menuItem = new JMenuItem("Filter...");
			ActionListener filter = createFilterListener();
			menuItem.addActionListener(filter);
			popup.add(menuItem);
		}
		{
			JMenuItem menuItem = new JMenuItem("Clear filter");
			menuItem.addActionListener(createClearFilterListener());
			popup.add(menuItem);
		}

	}

	private ActionListener createClearFilterListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				MyController.getController().event(new ClearFilter());
			}
		};
	}

	private ActionListener createFilterListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				MyController.getController().event(new EditFilter());
			}
		};
	}

	private ActionListener createExploreListener(final MusicFolder musicFolder) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				explore(musicFolder);
			}

			protected void explore(MusicFolder musicFolder) {
				String command = "gnome-open";
				if (System.getProperty("explore") != null)
					command = System.getProperty("explore");
				ProcessBuilder builder = new ProcessBuilder(command,
						musicFolder.getDirectory().getAbsolutePath());
				try {
					builder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	private ActionListener createPlayActionListener(
			final MusicFolder musicFolder) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				firePlay(musicFolder);
			}
		};
	}

	private ActionListener createFindArtworkActionListener(
			final MusicFolder musicFolder) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				findArtwork(musicFolder);
			}
		};
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
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					firePlay(musicFolder);
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					findArtwork(musicFolder);
				}
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}

		};
	}

	private void findArtwork(MusicFolder musicFolder) {
		AlbumArtFinder finder = new AlbumArtFinder(albumArt, musicFolder);
		finder.findCoverArt();
	}

	protected void firePlay(MusicFolder musicFolder) {
		for (AlbumCoverListener l : listeners)
			l.play(musicFolder);
	}

}
