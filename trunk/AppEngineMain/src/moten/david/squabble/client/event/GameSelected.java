package moten.david.squabble.client.event;

import moten.david.squabble.client.controller.Event;

public class GameSelected implements Event {
	private String gameId;

	public GameSelected(String gameId) {
		this.gameId = gameId;
	}

	public long getGameId() {
		return Long.parseLong(gameId);
	}
}
