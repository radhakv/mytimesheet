package moten.david.squabble.client;

import moten.david.squabble.client.controller.Controller;

import com.google.gwt.core.client.GWT;

public class Application {

	private static Application application;

	public synchronized static Application getInstance() {
		if (application == null)
			application = new Application();
		return application;
	}

	private Controller controller = new Controller();

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public Application() {

	}

}