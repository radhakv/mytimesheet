package moten.david.words.nines.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Nines implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final WordProviderServiceAsync wordProviderService = GWT
			.create(WordProviderService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final HTML answer = new HTML("");

		final HTML[] chars = new HTML[9];
		for (int i = 0; i < 9; i++) {
			RootPanel.get("c" + (i + 1)).add(chars[i] = new HTML());
		}

		final Button nextButton = new Button("Next");
		final Button answerButton = new Button("Show answer");

		RootPanel.get("nextButtonContainer").add(nextButton);
		RootPanel.get("answerButtonContainer").add(answerButton);
		RootPanel.get("answerContainer").add(answer);

		answerButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				answer.setVisible(true);

			}
		});

		// Add a handler to close the DialogBox
		nextButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				nextWord();
			}

			public void nextWord() {
				wordProviderService.getRandomWord(new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// log.error(caught.getMessage(), caught);
					}

					public void onSuccess(String result) {
						String scrambled = scramble(result);
						answer.setVisible(false);
						answer
								.setHTML("<p class=\"answer\">" + result
										+ "</p>");

						for (int i = 0; i < 9; i++)
							chars[i].setHTML("<p class=\"box\">"
									+ scrambled.charAt(i) + "</p>");
					}

					private String scramble(String word) {
						List list = new ArrayList();
						StringBuffer s = new StringBuffer();
						for (char ch : word.toCharArray()) {
							list.add(ch);
						}
						for (int i = list.size(); i >= 1; i--) {
							int index = (int) Math.round(Math.floor(Math
									.random()
									* list.size()));
							s.append(list.get(index) + "");
							list.remove(index);
						}
						return s.toString();
					}

				});

			}
		});
		nextButton.click();
	}

	public static void main(String[] args) {
		// System.out.println(scramble("hello"));
	}

}
