package moten.david.music.album.player;

import moten.david.swing.mvc.controller.Controller;

public class MyController {
	private static Controller controller;

	public static synchronized Controller getController() {
		if (controller == null)
			controller = new Controller();
		return controller;
	}
}
