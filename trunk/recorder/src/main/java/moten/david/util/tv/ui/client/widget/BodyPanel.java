package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.Refresh;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BodyPanel extends VerticalPanel {

	private final ProgrammePanel programmePanel;

	public BodyPanel() {
		setStyleName("body");
		Application.getInstance().getController().addListener(Refresh.class,
				createRefreshListener());
		programmePanel = new ProgrammePanel();
		setContent(programmePanel);
	}

	private ControllerListener<Refresh> createRefreshListener() {
		return new ControllerListener<Refresh>() {
			@Override
			public void event(Refresh event) {
				programmePanel.refresh();
			}
		};
	}

	public void setContent(Panel panel) {
		this.clear();
		add(panel);
	}

}
