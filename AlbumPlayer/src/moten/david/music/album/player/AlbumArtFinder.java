package moten.david.music.album.player;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import moten.david.music.album.art.AlbumArt;
import moten.david.music.album.art.Cache;
import moten.david.music.album.art.AlbumArt.UrlPair;

public class AlbumArtFinder extends JFrame {

	private final AlbumArt albumArt;
	private final MusicFolder musicFolder;
	private final JPanel panel;

	public AlbumArtFinder(AlbumArt albumArt, MusicFolder musicFolder) {
		super(musicFolder.getArtist() + " - " + musicFolder.getTitle());
		this.albumArt = albumArt;
		this.musicFolder = musicFolder;
		panel = new JPanel();
		panel.setLayout(new ModifiedFlowLayout());
		JScrollPane scroll = new JScrollPane(panel);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scroll);
		setSize(800, 600);
	}

	public void findCoverArt() {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				AlbumArtFinder.this.setVisible(true);
			}
		});
		Thread t = new Thread(new Runnable() {
			public void run() {
				final List<UrlPair> urls = albumArt.searchForImageUrls(
						musicFolder.getArtist(), musicFolder.getTitle());
				ArrayList<Thread> imageThreads = new ArrayList<Thread>();
				for (final UrlPair url : urls) {
					Thread thread = new Thread(createUrlLoader(
							AlbumArtFinder.this, panel, musicFolder, url));
					thread.start();
					panel.repaint();
				}
				for (Thread thread : imageThreads) {
					try {
						thread.join();
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}

		});
		t.start();

	}

	private Runnable createUrlLoader(final JFrame frame, final JPanel panel,
			final MusicFolder musicFolder, final UrlPair url) {
		return new Runnable() {
			public void run() {
				System.out.println("adding " + url);
				final String htmlPrefix = "<html><img src=\"";
				final String htmlSuffix = "\"></img></html>";
				final String smallUrl = htmlPrefix
						+ Cache.getFileUrl(url.getSmall()) + htmlSuffix;
				final String largeUrl = htmlPrefix
						+ Cache.getFileUrl(url.getLarge()) + htmlSuffix;
				final JLabel label = new JLabel(smallUrl);
				label.addMouseListener(new MouseAdapter() {
					private static final String marker = "._SL160_";

					@Override
					public void mouseClicked(MouseEvent event) {
						if (event.getButton() == MouseEvent.BUTTON1) {
							String s = label.getText();
							if (s.equals(smallUrl))
								label.setText(largeUrl);
							else
								label.setText(smallUrl);
							label.invalidate();
							panel.revalidate();
							repaint();
						} else if (event.getButton() == MouseEvent.BUTTON2) {
							musicFolder.saveImageToDirectory(url.getLarge());
							frame.setTitle(musicFolder.getArtist() + " - "
									+ musicFolder.getTitle() + ": Saved");
						}

					}
				});
				panel.add(label);
				panel.revalidate();
				panel.repaint();
				System.out.println("added " + url);
			}
		};
	}

}
