package moten.david.squabble.client.controller;


public interface ControllerListener<T extends Event> {
	void event(T event);
}
