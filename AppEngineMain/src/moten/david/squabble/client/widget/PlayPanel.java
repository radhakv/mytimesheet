package moten.david.squabble.client.widget;

import moten.david.squabble.client.Application;
import moten.david.squabble.client.controller.ControllerListener;
import moten.david.squabble.client.event.GameSelected;
import moten.david.squabble.client.event.NameSelected;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PlayPanel extends VerticalPanel {
	private String name;
	private final Label message = new Label();
	private Long gameId;

	public PlayPanel() {

		add(message);
		updateStatus();
		Application.getInstance().getController().addListener(
				NameSelected.class, createNameSelectedListener());
		Application.getInstance().getController().addListener(
				GameSelected.class, createGameSelectedListener());
	}

	private ControllerListener<GameSelected> createGameSelectedListener() {
		return new ControllerListener<GameSelected>() {
			@Override
			public void event(GameSelected event) {
				gameId = event.getGameId();
				updateStatus();
			}
		};
	}

	protected void updateStatus() {
		message.setText(name + " playing game " + gameId);
	}

	private ControllerListener<NameSelected> createNameSelectedListener() {
		return new ControllerListener<NameSelected>() {

			@Override
			public void event(NameSelected event) {
				name = event.getName();
				updateStatus();
			}
		};
	}
}
