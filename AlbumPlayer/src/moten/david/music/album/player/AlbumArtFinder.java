package moten.david.music.album.player;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import moten.david.music.album.art.AlbumArt;
import moten.david.music.album.art.Cache;
import moten.david.music.album.art.AlbumArt.UrlPair;

public class AlbumArtFinder extends JFrame {

	private static final long serialVersionUID = 6978041584714202456L;
	private final AlbumArt albumArt;
	private final MusicFolder musicFolder;
	private final JPanel panel;
	private final JTextField album = new JTextField();
	private final JTextField artist = new JTextField();
	private final JLabel status = new JLabel();

	public AlbumArtFinder(AlbumArt albumArt, MusicFolder musicFolder) {
		super(musicFolder.getArtist() + " - " + musicFolder.getTitle());
		this.albumArt = albumArt;
		this.musicFolder = musicFolder;
		panel = new JPanel();
		panel.setLayout(new ModifiedFlowLayout());
		JScrollPane scroll = new JScrollPane(panel);
		scroll
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(createSearchPanel(), BorderLayout.NORTH);
		getContentPane().add(scroll, BorderLayout.CENTER);
		album.setText(musicFolder.getTitle());
		artist.setText(musicFolder.getArtist());
		setSize(800, 600);
	}

	private Component createSearchPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("Album:"));
		panel.add(album);
		album.setPreferredSize(new Dimension(250, 25));
		KeyListener keyListener = createSearchKeyListener();
		album.addKeyListener(keyListener);
		panel.add(new JLabel("Artist:"));
		panel.add(artist);
		artist.setPreferredSize(new Dimension(200, 25));
		artist.addKeyListener(keyListener);
		JButton button = new JButton("Search");
		panel.add(button);
		button.addActionListener(createSearchButtonActionListener());
		panel.add(status);
		return panel;
	}

	private KeyListener createSearchKeyListener() {
		return new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					startSearch(artist.getText(), album.getText());
			}
		};

	}

	private ActionListener createSearchButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startSearch(artist.getText(), album.getText());
			}
		};
	}

	public void startSearch(final String artist, final String album) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				setStatus("Searching...");
				final List<UrlPair> urls = albumArt.searchForImageUrls(artist,
						album);
				panel.removeAll();
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
				setStatus("");
			}

		});
		t.start();
	}

	protected void setStatus(final String status) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				AlbumArtFinder.this.status.setText(status);
				panel.repaint();
			}
		});
	}

	public void findCoverArt() {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				AlbumArtFinder.this.setVisible(true);
			}
		});
		startSearch(musicFolder.getArtist(), musicFolder.getTitle());

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
							setStatus("Saving...");
							musicFolder.saveImageToDirectory(url.getLarge());
							setStatus("Saved");
							frame.setTitle(musicFolder.getArtist() + " - "
									+ musicFolder.getTitle());
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
