package moten.david.music.album.player;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import moten.david.music.album.art.AlbumArt;

public class AlbumViewerMain extends JFrame {
	private static final String MUSIC_HOME = "music.home";
	private static final String USER_HOME = "user.home";

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		javax.swing.UIManager.setLookAndFeel(UIManager
				.getSystemLookAndFeelClassName());

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Player player = new Player();
		String musicDirectory = System.getProperty(USER_HOME) + "/Music";
		if (System.getProperty(MUSIC_HOME) != null)
			musicDirectory = System.getProperty(MUSIC_HOME);
		final MusicFolderProvider musicFolderProvider = new MusicFolderProviderImpl(
				musicDirectory, new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getAbsolutePath().toUpperCase()
								.contains("");
					}
				});

		final ImageProvider imageProvider = new ImageProviderImpl();
		final AlbumArt albumArt = new AlbumArt();
		final AlbumViewer albumViewer = new AlbumViewer(musicFolderProvider,
				player, imageProvider, albumArt);
		AlbumViewerListener albumViewerListener = new AlbumViewerListener() {
			boolean randomize = false;

			@Override
			public void keyCode(int ch) {
				try {
					if (ch == KeyEvent.VK_F) {
						// Return to normal windowed mode
						if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)
							frame.setExtendedState(JFrame.NORMAL);
						else
							frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					} else if (ch == KeyEvent.VK_Q) {
						player.stop();
						frame.setVisible(false);
						frame.dispose();
					} else if (ch == KeyEvent.VK_N || ch == KeyEvent.VK_RIGHT) {
						player.write(">".getBytes());
						// player.forward();
					} else if (ch == KeyEvent.VK_P || ch == KeyEvent.VK_LEFT) {
						player.write("<".getBytes());
						// player.previous();
					} else if (ch == KeyEvent.VK_R) {
						randomize = !randomize;
						musicFolderProvider.setRandomize(randomize);
						albumViewer.redraw();
					} else
						player.write(new byte[] { (byte) ch });
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void viewChanged() {
				frame.setTitle("AlbumViewer "
						+ (albumViewer.getImageIndex() + 1)
						+ "-"
						+ (albumViewer.getImageIndex() + albumViewer
								.getImageCount()) + " of "
						+ (albumViewer.getMaxImageIndex() + 1));

			}
		};
		albumViewer.addListener(albumViewerListener);
		JScrollPane scroll = new JScrollPane(albumViewer);
		frame.getContentPane().add(albumViewer);
		albumViewer.requestFocus();

		frame.setTitle("Album Player - " + musicFolderProvider.getCount()
				+ " albums");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				player.stop();
			}

		});

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		int width = screenWidth * 10 / 16;
		int height = screenHeight * 10 / 16;
		frame.setSize(width, height);
		frame.setLocation((screenWidth - width) / 2,
				(screenHeight - height) / 2);

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

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				frame.setVisible(true);
			}
		});

	}
}
