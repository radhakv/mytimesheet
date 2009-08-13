package moten.david.squabble.client.play;

import moten.david.squabble.client.Application;
import moten.david.squabble.client.controller.ControllerListener;
import moten.david.squabble.client.event.NameSelected;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PlayPanel extends VerticalPanel {
	private String name;

	public PlayPanel() {
		add(new HTML("<p>game</p>"));
		Application.getInstance().getController().addListener(
				NameSelected.class, createNameSelectedListener());
	}

	private ControllerListener<NameSelected> createNameSelectedListener() {
		return new ControllerListener<NameSelected>() {

			@Override
			public void event(NameSelected event) {
				name = event.getName();
			}
		};
	}
}
