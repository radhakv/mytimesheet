package moten.david.squabble.client.widget;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class TitlePanel extends HorizontalPanel {

	public TitlePanel() {
		setStyleName("title");
		add(new HTML("<h2>Squabble</h2>"));
	}

}
