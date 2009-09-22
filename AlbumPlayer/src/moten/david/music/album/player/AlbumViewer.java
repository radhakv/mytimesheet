package moten.david.music.album.player;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class AlbumViewer extends JPanel {

	private static final long serialVersionUID = -682832479831122887L;
	private static int iconHeight = 200;
	private static int iconWidth = 200;
	private final MusicFolderProvider musicFolderProvider;
	private int imageIndex = 0;

	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	private int imageCount = 0;
	private final KeyListener keyListener;
	private final List<AlbumViewerListener> listeners = new ArrayList<AlbumViewerListener>();
	private final ImageProvider imageProvider;
	private final AlbumCoverListener albumCoverListener;

	public void addListener(AlbumViewerListener l) {
		listeners.add(l);
	}

	public AlbumViewer(MusicFolderProvider musicFolderProvider,
			final Player player, ImageProvider imageProvider) {
		this.musicFolderProvider = musicFolderProvider;
		this.imageProvider = imageProvider;
		FlowLayout layout = new FlowLayout();
		setLayout(layout);
		setFocusable(true);
		keyListener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode());
				{
					if (e.getKeyCode() == KeyEvent.VK_DOWN)
						next(1);
					else if (e.getKeyCode() == KeyEvent.VK_UP)
						previous(1);
					else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
						previous(1);
					else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
						next(1);
				}
				fireKey(e.getKeyCode());
			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		};
		addKeyListener(keyListener);
		albumCoverListener = createAlbumCoverListener(player);

	}

	private AlbumCoverListener createAlbumCoverListener(final Player player) {
		return new AlbumCoverListener() {

			@Override
			public void play(MusicFolder musicFolder) {
				player.play(musicFolder.getDirectory(), !"false".equals(System
						.getProperty("shuffle")));
			}
		};
	}

	protected void fireKey(int keyCode) {
		for (AlbumViewerListener l : listeners)
			l.keyCode(keyCode);
	}

	protected void fireViewChanged() {
		for (AlbumViewerListener l : listeners)
			l.viewChanged();
	}

	private synchronized void next(int pages) {
		System.out.println("next");
		imageIndex += imageCount * pages;
		if (imageIndex < 0)
			imageIndex = 0;
		if (imageIndex >= musicFolderProvider.getCount())
			imageIndex = musicFolderProvider.getCount() - imageCount;
		redraw();
	}

	private void redraw() {
		imageCount = updateImages(imageIndex);
		fireViewChanged();
	}

	private void previous(int pages) {
		System.out.println("previous");
		next(-pages);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (imageCount != getNumberThatFitInCurrentSize())
			redraw();
	}

	private int getNumberThatFitInCurrentSize() {
		int numHorizontal = getWidth() / iconWidth;
		int numVertical = getHeight() / iconHeight;
		int number = numHorizontal * numVertical;
		return number;
	}

	private int updateImages(int start) {
		removeAll();
		System.out.println(getSize());
		int number = getNumberThatFitInCurrentSize();
		System.out.println(number);
		List<MusicFolder> musicFolders = musicFolderProvider.getMusicFolders(
				"all", start, number);
		if (musicFolders.size() < number) {
			int adjustedStart = start + musicFolders.size() - number;
			if (adjustedStart < 0)
				adjustedStart = 0;
			musicFolders = musicFolderProvider.getMusicFolders("all",
					adjustedStart, number);
		}

		for (MusicFolder musicFolder : musicFolders) {
			// ImageIcon icon = new javax.swing.ImageIcon(image.getImage());
			AlbumCover cover = new AlbumCover(musicFolder, imageProvider,
					iconWidth, iconHeight);
			cover.addListener(albumCoverListener);
			add(cover);
			cover.repaint();
		}
		revalidate();
		repaint();
		return number;
	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		javax.swing.UIManager.setLookAndFeel(UIManager
				.getSystemLookAndFeelClassName());

		final JFrame frame = new JFrame();
		// win.setFocusable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Player player = new Player();
		String musicDirectory = System.getProperty("user.home") + "/Music";
		final MusicFolderProvider musicFolderProvider = new MusicFolderProviderImpl(
				musicDirectory);
		final ImageProvider imageProvider = new ImageProviderImpl();
		final AlbumViewer albumViewer = new AlbumViewer(musicFolderProvider,
				player, imageProvider);
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
								iconWidth, iconHeight);
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

	protected int getImageCount() {
		return imageCount;
	}

	protected int getMaxImageIndex() {
		return musicFolderProvider.getCount() - 1;
	}
}
