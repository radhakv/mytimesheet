package moten.david.squabble.client;

import moten.david.squabble.client.controller.ControllerListener;
import moten.david.squabble.client.event.GameSelected;
import moten.david.squabble.client.event.PlayGame;
import moten.david.squabble.client.event.SelectGame;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MenuPanel extends VerticalPanel {

	public MenuPanel() {
		setStyleName("menu");
		Button selectGame = createMenuItem("Select game");
		add(selectGame);
		final Button playGame = createMenuItem("Play");
		add(playGame);
		playGame.setVisible(false);
		selectGame.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Application.getInstance().getController().event(
						new SelectGame());
			}
		});
		playGame.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Application.getInstance().getController().event(new PlayGame());
			}
		});
		Application.getInstance().getController().addListener(
				GameSelected.class, new ControllerListener<GameSelected>() {
					@Override
					public void event(GameSelected event) {
						playGame.setVisible(true);
					}
				});

	}

	private Button createMenuItem(String label) {
		Button button = new Button(label);
		button.setStyleName("menuItem");
		return button;
	}

}
