package moten.david.squabble.client;

import moten.david.squabble.client.controller.Controller;


public class Application {
	private Controller controller = new Controller();
	private static Application application;

	public synchronized static Application getInstance() {
		if (application == null)
			application = new Application();
		return application;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}