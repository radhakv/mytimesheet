package moten.david.squabble.client;

import moten.david.squabble.client.controller.Controller;
import moten.david.squabble.client.controller.ControllerListener;
import moten.david.squabble.client.event.GameSelected;
import moten.david.squabble.client.event.NewGameRequested;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
		controller.addListener(NewGameRequested.class,
				new ControllerListener<NewGameRequested>() {

					@Override
					public void event(NewGameRequested event) {
						applicationService.newGame(new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO
							}

							@Override
							public void onSuccess(String result) {
								controller.event(new GameSelected(result));
							}
						});
					}
				});
	}
}
