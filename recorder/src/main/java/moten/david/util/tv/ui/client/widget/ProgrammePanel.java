package moten.david.util.tv.ui.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import moten.david.util.tv.ui.client.Application;
import moten.david.util.tv.ui.client.ApplicationService;
import moten.david.util.tv.ui.client.ApplicationServiceAsync;
import moten.david.util.tv.ui.client.MyProgrammeItem;
import moten.david.util.tv.ui.client.controller.ControllerListener;
import moten.david.util.tv.ui.client.event.ShowProgramme;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgrammePanel extends VerticalPanel {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);
	private final AsyncCallback<MyProgrammeItem[]> getProgrammeCallback;
	private final FlexTable table;
	private final List<String> channels = new ArrayList<String>();

	public ProgrammePanel() {
		Application.getInstance().getController().addListener(
				ShowProgramme.class, createRefreshListener());
		getProgrammeCallback = createGetProgrammeCallback();
		table = new FlexTable();
		table.setStyleName("programme");

		channels.add("SBS-Can");
		channels.add("SBSTWO-NSW");
		channels.add("One-NSW");
		channels.add("ABC-Can");
		channels.add("ABC2");
		channels.add("Prime-Can");
		channels.add("Ten-Can");
		channels.add("WIN-Can");
		channels.add("GO");
	}

	private AsyncCallback<MyProgrammeItem[]> createGetProgrammeCallback() {
		return new AsyncCallback<MyProgrammeItem[]>() {

			@Override
			public void onFailure(Throwable t) {
				add(new HTML(t.getMessage()));
			}

			@Override
			public void onSuccess(MyProgrammeItem[] items) {
				try {
					Date now = new Date();

					// int nowMinutes = now.getHours() * 60 + now.getMinutes();
					int totalExtraSpan = 0;
					String lastChannelId = null;
					Date bestStartTime = null;
					for (final MyProgrammeItem item : items) {
						if (bestStartTime == null
								|| item.getStart().before(bestStartTime))
							bestStartTime = item.getStart();
					}
					if (bestStartTime == null)
						bestStartTime = new Date();

					for (final MyProgrammeItem item : items) {
						if (item.getStop().after(bestStartTime)) {
							if (!item.getChannelId().equals(lastChannelId))
								totalExtraSpan = 0;
							int row = channels.indexOf(item.getChannelId());
							{
								Label label = new Label(item.getChannelId());
								table.setWidget(row, 0, label);
								label.setStyleName("channel");
							}
							int col = (int) ((item.getStart().getTime() - bestStartTime
									.getTime())
									/ (5 * 60000l) + 1);
							col -= totalExtraSpan;
							int span = (int) ((item.getStop().getTime() - item
									.getStart().getTime()) / (60000l * 5));
							totalExtraSpan += span - 1;

							String startTime = (item.getStartTimeInMinutes() / 60)
									+ "";
							if (startTime.length() == 1)
								startTime = "0" + startTime;
							startTime += ":";
							String minutes = (item.getStartTimeInMinutes() % 60)
									+ "";
							if (minutes.length() == 1)
								minutes = "0" + minutes;
							startTime += minutes;
							{
								VerticalPanel vp = new VerticalPanel();
								vp.setStyleName("noBorder");
								Label labelTime = new Label(startTime);
								boolean isOnNow = item.getStart().before(now)
										&& item.getStop().after(now);
								if (isOnNow)
									labelTime.setStyleName("currentTime");
								else
									labelTime.setStyleName("time");
								vp.add(labelTime);
								ClickHandler clickHandler = createPlayClickHandler(item
										.getChannelId());
								DisclosurePanel disclosureTitle = new DisclosurePanel();
								Label labelTitle = new Label(item.getTitle());
								labelTitle.setStyleName("itemTitle");
								disclosureTitle.setHeader(labelTitle);
								vp.add(disclosureTitle);
								{
									VerticalPanel content = new VerticalPanel();
									Label text = new Label();
									String desc = item.getDescription();
									// if (desc != null && desc.length() > 250)
									// desc = desc.substring(0, 250);
									text.setText(desc);
									text.setStyleName("itemDescription");
									content.add(text);
									HorizontalPanel p = new HorizontalPanel();
									Button play = new Button("Play");
									play.setStyleName("play");
									if (isOnNow)
										p.add(play);
									DisclosurePanel record = new DisclosurePanel();
									p.add(record);
									{
										Label recordLabel = new Label("Record");
										recordLabel.setStyleName("record");
										record.setHeader(recordLabel);
										Panel p2 = new HorizontalPanel();
										final CheckBox highQuality = new CheckBox(
												"High Quality");
										p2.add(highQuality);
										Button recordButton = new Button(
												"Record");
										p2.add(recordButton);
										record.setContent(p2);
										recordButton
												.addClickHandler(createRecordClickHandler(item));
									}
									content.add(p);
									play.addClickHandler(clickHandler);
									disclosureTitle.setContent(content);
									text.addClickHandler(clickHandler);
								}

								table.setWidget(row, col, vp);
							}
							table.getFlexCellFormatter().setColSpan(row, col,
									span);
							table.getRowFormatter().setStyleName(row,
									"programmeRow");
							table.getFlexCellFormatter().setStyleName(row, col,
									"programmeItem");
							lastChannelId = item.getChannelId();
						}

					}

					for (int row = 0; row < table.getRowCount(); row++) {
						table.getCellFormatter().setStyleName(row, 0,
								"channelCell");
						int span = 1;
						int col = 1;
						while (table.getWidget(row, col) == null
								&& col < table.getCellCount(row)) {
							table.getFlexCellFormatter().setStyleName(row, col,
									"programmeItemEmpty");
							span++;
							col++;
						}
					}

				} catch (RuntimeException e) {
					add(new Label(e.toString()));
				}
			}

		};
	}

	private ClickHandler createPlayClickHandler(final String channelId) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				applicationService.play(channelId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable arg0) {
						add(new Label(arg0.getMessage()));
					}

					@Override
					public void onSuccess(Void arg0) {
						// do nothing
					}
				});
			}
		};
	}

	private ClickHandler createRecordClickHandler(final MyProgrammeItem item) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				applicationService.record(item.getTitle(), item.getChannelId(),
						item.getStart(), item.getStop(),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable arg0) {
								add(new Label(arg0.getMessage()));
							}

							@Override
							public void onSuccess(Void arg0) {
								// do nothing
							}
						});
			}
		};
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
		table.clear();
		add(table);
		Date now = new Date();
		applicationService.getProgramme(channels.toArray(new String[] {}), now,
				new Date(now.getTime() + 24 * 3600 * 1000),
				getProgrammeCallback);
	}
}
