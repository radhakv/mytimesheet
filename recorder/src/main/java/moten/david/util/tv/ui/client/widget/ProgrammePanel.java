package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.ShowProgramme;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgrammePanel extends VerticalPanel {

	public ProgrammePanel() {
		Application.getInstance().getController().addListener(ShowProgramme.class,
				createRefreshListener());
	}

	private ControllerListener<ShowProgramme> createRefreshListener() {
		return new ControllerListener<ShowProgramme>() {

			@Override
			public void event(ShowProgramme event) {
				refresh();
			}

		};
	}

	public void refresh() {
		clear();
		add(new Label("Hello"));
	}

}
