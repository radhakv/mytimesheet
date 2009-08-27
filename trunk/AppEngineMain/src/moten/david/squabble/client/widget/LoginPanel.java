package moten.david.squabble.client.widget;

import moten.david.squabble.client.Application;
import moten.david.squabble.client.event.NameSelected;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class LoginPanel extends HorizontalPanel {

	public LoginPanel() {
		setStyleName("login");
		Label label = new Label("Name:");
		label.setStyleName("loginLabel");
		add(label);
		TextBox name = new TextBox();
		add(name);
		name.setText(Cookies.getCookie("player.name"));
		name.addChangeHandler(createChangeHandler(name));
	}

	private ChangeHandler createChangeHandler(final TextBox name) {

		return new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				Cookies.setCookie("player.name", name.getText());
				Application.getInstance().getController().event(
						new NameSelected(name.getText()));
			}

		};
	}

}
