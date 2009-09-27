package moten.david.music.album.player;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicFolderProviderImpl implements MusicFolderProvider {

	private ArrayList<File> files;
	private ArrayList<File> filesOriginal;
	private boolean randomized = false;
	private FileFilter filter;
	private final File musicDirectory;

	public MusicFolderProviderImpl(String musicDirectory, FileFilter filter) {
		this.musicDirectory = new File(musicDirectory);
		this.filter = filter;
		loadFiles();
	}

	public int getCount() {
		return files.size();
	}

	private void loadFiles() {
		files = new ArrayList<File>();
		addMusicFolders(musicDirectory, files, filter);
		filesOriginal = new ArrayList<File>(files);
		System.out.println(files.toString());
		System.out.println(files.size() + " images");
	}

	private void addMusicFolders(File file, ArrayList<File> files,
			FileFilter filter) {
		if (file.isDirectory()) {
			File bestFile = null;
			Long bestSize = null;
			boolean hasAudio = false;
			for (File f : file.listFiles()) {
				if (Util.isAudio(f))
					hasAudio = true;
				String name = f.getName().toUpperCase();
				if (name.endsWith(".JPG") && !name.endsWith(" BACK.JPG")
						&& f.length() < 1000000
						&& (bestSize == null || bestSize < f.length())) {
					bestFile = f;
					bestSize = f.length();
				}
			}
			if (hasAudio && bestFile != null)
				addToFiles(files, bestFile, filter);
			else if (hasAudio)
				addToFiles(files, file, filter);

			File[] list = file.listFiles();
			Arrays.sort(list, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					return f1.getName().compareTo(f2.getName());
				}
			});
			for (File f : list)
				if (f.isDirectory())
					addMusicFolders(f, files, filter);
		}
	}

	private void addToFiles(ArrayList<File> files, File file, FileFilter filter) {
		if (filter == null || filter.accept(file))
			files.add(file);
	}

	@Override
	public List<MusicFolder> getMusicFolders(String key, final int startIndex,
			final int number) {

		ArrayList<MusicFolder> list = new ArrayList<MusicFolder>() {
			{
				for (int i = startIndex; i < startIndex + number
						&& i < files.size(); i++) {
					File file = files.get(i);
					if (file.isDirectory())
						add(new MusicFolder(null, file.getParentFile()
								.getName(), file.getName(), "comments", file));
					else
						add(new MusicFolder(file, file.getParentFile()
								.getParentFile().getName(), file
								.getParentFile().getName(), "comments", file
								.getParentFile()));
				}
			}
		};

		return list;
	}

	@Override
	public void setRandomize(boolean value) {
		randomized = value;
		if (randomized) {
			files = new ArrayList<File>(filesOriginal);
			Collections.shuffle(files);
		} else
			files = filesOriginal;
	}

	@Override
	public boolean getRandomize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFilter(FileFilter filter) {
		this.filter = filter;
		loadFiles();
	}

	@Override
	public FileFilter getFilter() {
		return filter;
	}

}
