package moten.david.squabble.client;

import java.io.Serializable;
import java.util.Date;

public class MyWord implements Serializable {
	private long id;
	private String value;
	private String owner;
	private boolean isVisible;
	private Long becameWord;
	private String gameId;
	private Date timeCreated;
	private Date timeVisible;
	private Date timeBecameWord;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Long getBecameWord() {
		return becameWord;
	}

	public void setBecameWord(Long becameWord) {
		this.becameWord = becameWord;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public Date getTimeVisible() {
		return timeVisible;
	}

	public void setTimeVisible(Date timeVisible) {
		this.timeVisible = timeVisible;
	}

	public Date getTimeBecameWord() {
		return timeBecameWord;
	}

	public void setTimeBecameWord(Date timeBecameWord) {
		this.timeBecameWord = timeBecameWord;
	}
}
