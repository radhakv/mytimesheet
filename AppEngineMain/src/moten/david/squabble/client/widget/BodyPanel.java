package moten.david.squabble.client.widget;

import moten.david.squabble.client.Application;
import moten.david.squabble.client.controller.ControllerListener;
import moten.david.squabble.client.event.Login;
import moten.david.squabble.client.event.PlayGame;
import moten.david.squabble.client.event.SelectGame;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BodyPanel extends VerticalPanel {

	private PlayPanel playPanel;

	public BodyPanel() {
		setStyleName("body");
		Application.getInstance().getController().addListener(SelectGame.class,
				createSelectGameListener());
		Application.getInstance().getController().addListener(PlayGame.class,
				createPlayGameListener());
		Application.getInstance().getController().addListener(Login.class,
				createLoginListener());
		playPanel = new PlayPanel();
	}

	private ControllerListener<Login> createLoginListener() {
		return new ControllerListener<Login>() {
			@Override
			public void event(Login event) {
				setContent(new LoginPanel());
			}
		};
	}

	public void setContent(Panel panel) {
		this.clear();
		add(panel);
	}

	private ControllerListener<PlayGame> createPlayGameListener() {
		return new ControllerListener<PlayGame>() {
			@Override
			public void event(PlayGame event) {
				setContent(playPanel);
			}
		};
	}

	private ControllerListener<SelectGame> createSelectGameListener() {
		return new ControllerListener<SelectGame>() {

			@Override
			public void event(SelectGame event) {
				setContent(new SelectGamePanel());
			}
		};
	}
}
