package moten.david.squabble.client;

import moten.david.squabble.client.widget.AppPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationClient implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get("container").add(new AppPanel());
	}
}
