package moten.david.music.album.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicFolderProviderImpl implements MusicFolderProvider {

	private ArrayList<File> files;
	private final ArrayList<File> filesOriginal;
	private boolean randomized = false;

	public MusicFolderProviderImpl(String musicDirectory) {
		File file = new File(musicDirectory);
		files = new ArrayList<File>();
		addImages(file, files);
		filesOriginal = new ArrayList<File>(files);
		System.out.println(files.toString());
		System.out.println(files.size() + " images");
	}

	public int getCount() {
		return files.size();
	}

	private void addImages(File file, ArrayList<File> files) {
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
				files.add(bestFile);
			else if (hasAudio)
				files.add(file);

			File[] list = file.listFiles();
			Arrays.sort(list, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return f1.getName().compareTo(f2.getName());
				}
			});
			for (File f : list)
				if (f.isDirectory())
					addImages(f, files);
		}
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

}
