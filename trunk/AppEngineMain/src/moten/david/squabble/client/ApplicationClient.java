package moten.david.squabble.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class ApplicationClient implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);

	@Override
	public void onModuleLoad() {

	}
}
