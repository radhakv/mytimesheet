package moten.david.music.album.player.event;

import moten.david.music.album.player.MusicFolder;
import moten.david.swing.mvc.controller.Event;

public class Play implements Event {

	private final MusicFolder musicFolder;

	public Play(MusicFolder musicFolder) {
		this.musicFolder = musicFolder;

	}

	public MusicFolder getMusicFolder() {
		return musicFolder;
	}

}
