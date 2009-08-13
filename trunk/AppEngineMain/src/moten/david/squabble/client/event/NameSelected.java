package moten.david.squabble.client.event;

import moten.david.squabble.client.controller.Event;

public class NameSelected implements Event {

	private String name;

	public String getName() {
		return name;
	}

	public NameSelected(String name) {
		this.name = name;
	}

}
