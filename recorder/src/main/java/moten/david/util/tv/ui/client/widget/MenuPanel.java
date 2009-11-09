package moten.david.util.tv.ui.client.widget;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.event.Refresh;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuPanel extends VerticalPanel {

	public MenuPanel() {

		setStyleName("menu");
		Button showProgramme = createMenuItem("Programme");
		add(showProgramme);
		showProgramme.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Application.getInstance().getController().event(new Refresh());
			}
		});
	}

	private Button createMenuItem(String label) {
		Button button = new Button(label);
		button.setStyleName("menuItem");
		return button;
	}

}
