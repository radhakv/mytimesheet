package moten.david.squabble.client;

import java.util.Date;

import moten.david.squabble.client.event.GameSelected;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectGamePanel extends VerticalPanel {

	public SelectGamePanel() {
		add(new HTML("<p>" + new Date() + "</p>"));
		Button select = new Button("Select");
		add(select);
		select.addClickHandler(createSelectClickHandler());
	}

	private ClickHandler createSelectClickHandler() {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Application.getInstance().getController().event(
						new GameSelected());
			}
		};

	}
}
