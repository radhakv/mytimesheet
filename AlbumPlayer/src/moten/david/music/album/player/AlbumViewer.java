package moten.david.music.album.player;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import moten.david.music.album.art.AlbumArt;
import moten.david.music.album.player.event.EditFilter;

public class AlbumViewer extends JPanel {

	private static final long serialVersionUID = -682832479831122887L;
	private int iconHeight = 200;
	private int iconWidth = iconHeight;

	public int getIconHeight() {
		return iconHeight;
	}

	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}

	private final MusicFolderProvider musicFolderProvider;
	private int imageIndex = 0;

	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	private int imageCount = 0;
	private final List<AlbumViewerListener> listeners = new ArrayList<AlbumViewerListener>();
	private final ImageProvider imageProvider;
	private final AlbumCoverListener albumCoverListener;
	private final AlbumArt albumArt;
	private final JPopupMenu popup;

	public void addListener(AlbumViewerListener l) {
		listeners.add(l);
	}

	public AlbumViewer(MusicFolderProvider musicFolderProvider,
			final Player player, ImageProvider imageProvider, AlbumArt albumArt) {
		this.musicFolderProvider = musicFolderProvider;
		this.imageProvider = imageProvider;
		this.albumArt = albumArt;
		FlowLayout layout = new FlowLayout();
		setLayout(layout);
		setFocusable(true);
		KeyListener keyListener = createKeyListener();
		addKeyListener(keyListener);
		albumCoverListener = createAlbumCoverListener(player);
		addMouseListener(createMouseListener());
		popup = new JPopupMenu();
		{
			JMenuItem menuItem = new JMenuItem("Filter...");
			menuItem.addActionListener(createFilterListener());
			popup.add(menuItem);
		}

	}

	private MouseListener createMouseListener() {
		return new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
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

	private KeyListener createKeyListener() {
		return new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode());
				{
					if (e.getKeyCode() == KeyEvent.VK_DOWN)
						nextRow(1);
					else if (e.getKeyCode() == KeyEvent.VK_UP)
						nextRow(-1);
					else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
						previous(1);
					else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
						next(1);
				}
				fireKey(e.getKeyCode());
			}

		};
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

	private synchronized void nextRow(int rows) {
		System.out.println("next");
		int rowSize = getWidth() / iconWidth;
		imageIndex += rowSize * rows;
		if (imageIndex < 0)
			imageIndex = 0;
		if (imageIndex >= musicFolderProvider.getCount())
			imageIndex = musicFolderProvider.getCount() - imageCount;
		redraw();
	}

	void redraw() {
		imageCount = updateImages(imageIndex);
		fireViewChanged();
	}

	private void previous(int pages) {
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
					iconWidth, iconHeight, albumArt);
			cover.addListener(albumCoverListener);
			add(cover);
			cover.repaint();
		}
		revalidate();
		repaint();
		return number;
	}

	protected int getImageCount() {
		return imageCount;
	}

	protected int getMaxImageIndex() {
		return musicFolderProvider.getCount() - 1;
	}
}
