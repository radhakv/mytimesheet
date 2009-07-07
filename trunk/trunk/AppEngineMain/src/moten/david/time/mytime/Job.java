package moten.david.time.mytime;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Key;

@Entity
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key id;
	@Enumerated
	private List<Key> entries = new ArrayList<Key>();
	@Enumerated
	private String name;
	@Enumerated
	private String group;
	@Enumerated
	private String reference;

	@Enumerated
	private Key user;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public List<Key> getEntries() {
		return entries;
	}

	public void setEntries(List<Key> entries) {
		this.entries = entries;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Key getUser() {
		return user;
	}

	public void setUser(Key user) {
		this.user = user;
	}

}
