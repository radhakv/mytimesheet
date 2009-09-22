package moten.david.music.album.player;

import java.util.List;

public interface MusicFolderProvider {
	List<MusicFolder> getMusicFolders(String key, int startIndex, int number);

	int getCount();

	void setRandomize(boolean value);

	boolean getRandomize();
}
