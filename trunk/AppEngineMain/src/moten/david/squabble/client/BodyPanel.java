package moten.david.squabble.client;

import moten.david.squabble.client.controller.ControllerListener;
import moten.david.squabble.client.event.PlayGame;
import moten.david.squabble.client.event.SelectGame;
import moten.david.squabble.client.play.PlayPanel;

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
		playPanel = new PlayPanel();
	}

	private ControllerListener<PlayGame> createPlayGameListener() {
		return new ControllerListener<PlayGame>() {
			@Override
			public void event(PlayGame event) {
				setContent(playPanel);
			}
		};
	}

	public void setContent(Panel panel) {
		this.clear();
		add(panel);
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
