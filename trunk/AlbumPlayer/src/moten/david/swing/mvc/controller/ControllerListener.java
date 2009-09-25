package moten.david.swing.mvc.controller;


public interface ControllerListener<T extends Event> {
	void event(T event);
}
