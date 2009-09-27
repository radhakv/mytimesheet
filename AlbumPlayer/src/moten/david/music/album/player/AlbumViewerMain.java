package moten.david.music.album.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import moten.david.music.album.art.AlbumArt;
import moten.david.music.album.player.event.ClearFilter;
import moten.david.music.album.player.event.EditFilter;
import moten.david.swing.mvc.controller.ControllerListener;

public class AlbumViewerMain extends JFrame {
	private static final long serialVersionUID = 8441183186297928751L;
	private static final String MUSIC_HOME = "music.home";
	private static final String USER_HOME = "user.home";
	private final List<String> filterKeywords;
	private MusicFolderProviderImpl musicFolderProvider;
	private AlbumViewer albumViewer;
	private String musicDirectory;

	public AlbumViewerMain() {

		musicDirectory = System.getProperty(USER_HOME) + "/Music";
		if (System.getProperty(MUSIC_HOME) != null)
			musicDirectory = System.getProperty(MUSIC_HOME);
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		musicDirectory = prefs.get("music.home", musicDirectory);
		filterKeywords = new ArrayList<String>();
		loadMusic();
	}

	private void loadMusic() {
		getContentPane().removeAll();
		final Player player = new Player();
		musicFolderProvider = new MusicFolderProviderImpl(musicDirectory,
				createFileFilter());
		final ImageProvider imageProvider = new ImageProviderImpl();
		final AlbumArt albumArt = new AlbumArt();
		albumViewer = new AlbumViewer(musicFolderProvider, player,
				imageProvider, albumArt);
		AlbumViewerListener albumViewerListener = new AlbumViewerListener() {
			boolean randomize = false;

			@Override
			public void keyCode(int ch) {
				try {
					if (ch == KeyEvent.VK_F) {
						// Return to normal windowed mode
						if (getExtendedState() == JFrame.MAXIMIZED_BOTH)
							setExtendedState(JFrame.NORMAL);
						else
							setExtendedState(JFrame.MAXIMIZED_BOTH);
					} else if (ch == KeyEvent.VK_Q) {
						player.stop();
						setVisible(false);
						dispose();
					} else if (ch == KeyEvent.VK_N || ch == KeyEvent.VK_RIGHT) {
						player.write(">".getBytes());
						// player.forward();
					} else if (ch == KeyEvent.VK_P || ch == KeyEvent.VK_LEFT) {
						player.write("<".getBytes());
						// player.previous();
					} else if (ch == KeyEvent.VK_R) {
						randomize = !randomize;
						musicFolderProvider.setShuffled(randomize);
						albumViewer.redraw();
					} else
						player.write(new byte[] { (byte) ch });
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void viewChanged() {
				setTitle("AlbumViewer "
						+ (albumViewer.getImageIndex() + 1)
						+ "-"
						+ (albumViewer.getImageIndex() + albumViewer
								.getImageCount()) + " of "
						+ (albumViewer.getMaxImageIndex() + 1));

			}
		};
		albumViewer.addListener(albumViewerListener);
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(albumViewer, BorderLayout.CENTER);
		albumViewer.requestFocus();

		setTitle("Album Player - " + musicFolderProvider.getCount() + " albums");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				player.stop();
			}

		});

		MyController.getController().addListener(EditFilter.class,
				new ControllerListener<EditFilter>() {
					@Override
					public void event(EditFilter event) {
						editFilter();
					}
				});

		MyController.getController().addListener(ClearFilter.class,
				new ControllerListener<ClearFilter>() {
					@Override
					public void event(ClearFilter event) {
						clearFilter();
					}
				});

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				for (MusicFolder musicFolder : musicFolderProvider
						.getMusicFolders("all", 0, Integer.MAX_VALUE)) {
					if (musicFolder.getImage() != null)
						imageProvider.getImage(musicFolder.getImage(),
								albumViewer.getIconWidth(), albumViewer
										.getIconHeight());
				}
			}
		});
		t.start();

	}

	protected void clearFilter() {
		updateFilter();
		resetFolderProviderAndRedraw();
	}

	private void updateFilter(String... keywords) {
		filterKeywords.clear();
		for (String s : keywords)
			filterKeywords.add(s);
		System.out.println(filterKeywords);
	}

	private FileFilter createFileFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (filterKeywords.size() == 0)
					return true;
				String path = file.getAbsolutePath()
						.replace(musicDirectory, "").toUpperCase();
				for (String keyword : filterKeywords)
					if (path.contains(keyword.toUpperCase()))
						return true;
				return false;
			}
		};
	}

	private void resetFolderProviderAndRedraw() {
		musicFolderProvider.setFilter(musicFolderProvider.getFilter());
		albumViewer.setImageIndex(0);
		albumViewer.redraw();
		repaint();
	}

	public void editFilter() {

		final String s = JOptionPane.showInputDialog(this, "Filter:",
				concatenateFilterKeywords());
		System.out.println("filter keywords=" + s);
		if (s != null) {
			updateFilter(s);
			resetFolderProviderAndRedraw();
		}
	}

	private String concatenateFilterKeywords() {
		StringBuffer s = new StringBuffer();
		for (String keyword : filterKeywords) {
			if (s.length() > 0)
				s.append(" ");
			s.append(keyword);
		}
		return s.toString();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		javax.swing.UIManager.setLookAndFeel(UIManager
				.getSystemLookAndFeelClassName());
		final AlbumViewerMain frame = new AlbumViewerMain();
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		int width = screenWidth * 10 / 16;
		int height = screenHeight * 10 / 16;
		frame.setSize(width, height);
		frame.setLocation((screenWidth - width) / 2,
				(screenHeight - height) / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				frame.setVisible(true);
			}
		});

	}
}
