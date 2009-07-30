package moten.david.squabble;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity(name = "Word")
public class Word {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	@Column
	private String value;
	@Column
	private String owner;
	@Column
	private boolean isVisible;
	@Column
	private Key becameWord;
	@Column
	private String gameId;
	@Column
	private Date timeCreated;
	@Column
	private Date timeVisible;
	@Column
	private Date timeBecameWord;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
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

	public Key getBecameWord() {
		return becameWord;
	}

	public void setBecameWord(Key becameWord) {
		this.becameWord = becameWord;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}
}
