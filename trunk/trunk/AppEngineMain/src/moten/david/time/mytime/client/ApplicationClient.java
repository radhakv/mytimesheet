package moten.david.time.mytime.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class ApplicationClient implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final ApplicationServiceAsync applicationService = GWT
			.create(ApplicationService.class);
	private Button add;
	private final Label date = new Label();
	private final TextBox startTime = new TextBox();
	private final TextBox endTime = new TextBox();
	private final TextArea information = new TextArea();
	private final TextArea exportText = new TextArea();
	private final Label day = new Label();
	private final VerticalPanel tablePanel = new VerticalPanel();
	private final VerticalPanel importPanel = new VerticalPanel();
	private final FlexTable headerTable = new FlexTable();

	private final Button entriesButton = new Button("entries");

	private final Button importButton = new Button("import");
	private final TextArea importText = new TextArea();
	private final Button importSubmit = new Button("import");
	private final Button exportButton = new Button("export");
	private final Button datesButton = new Button("dates");
	private final Button reportButton = new Button("report");
	private final VerticalPanel reportPanel = new VerticalPanel();
	private final DatePicker startDate = new DatePicker();
	private final DatePicker endDate = new DatePicker();
	private static final DateTimeFormat dateFormat = DateTimeFormat
			.getFormat("dd/MM/yyyy");
	private static final DateTimeFormat shortDateFormat = DateTimeFormat
			.getFormat("dd/MM/yy");
	private static final DateTimeFormat dayFormat = DateTimeFormat
			.getFormat("EEEE");

	private static final DateTimeFormat yearFormat = DateTimeFormat
			.getFormat("yyyy");
	private static final DateTimeFormat monthFormat = DateTimeFormat
			.getFormat("MM");
	private static final DateTimeFormat monthDayFormat = DateTimeFormat
			.getFormat("dd");
	private static final long MILLIS_PER_HOUR = 3600l * 1000l;
	private static final long MILLIS_PER_MINUTE = 60000;

	private EntriesCallback entriesCallback = new EntriesCallback();
	public ExportCallback exportCallback = new ExportCallback();
	private Label jobName;
	private VerticalPanel mainPanel;
	private HorizontalPanel headerPanel;
	private HorizontalPanel bodyPanel;
	private VerticalPanel menuPanel;
	private VerticalPanel contentPanel;
	private VerticalPanel entriesPanel;
	private HorizontalPanel datesPanel;
	private VerticalPanel exportPanel;

	@Override
	public void onModuleLoad() {

		mainPanel = new VerticalPanel();

		headerPanel = new HorizontalPanel();
		bodyPanel = new HorizontalPanel();
		mainPanel.add(headerPanel);
		mainPanel.add(bodyPanel);
		menuPanel = new VerticalPanel();
		contentPanel = new VerticalPanel();
		bodyPanel.add(menuPanel);
		bodyPanel.add(contentPanel);

		entriesPanel = new VerticalPanel();
		datesPanel = new HorizontalPanel();
		exportPanel = new VerticalPanel();

		jobName = new Label("Time");

		headerPanel.add(jobName);

		menuPanel.add(entriesButton);
		menuPanel.add(datesButton);
		menuPanel.add(reportButton);
		menuPanel.add(importButton);
		menuPanel.add(exportButton);
		Anchor logout = new Anchor("logout", "mytime?logout=true");
		logout.setStyleName("menuItem");
		menuPanel.add(logout);

		exportText.setVisible(false);
		bodyPanel.add(menuPanel);
		bodyPanel.add(contentPanel);

		datesPanel.add(startDate);
		datesPanel.add(endDate);

		setDate(new Date());

		startTime.setText("");
		endTime.setText("");

		headerPanel.add(information);

		entriesPanel.add(headerTable);
		entriesPanel.add(tablePanel);

		exportPanel.add(exportText);

		importPanel.add(importText);
		importPanel.add(importSubmit);

		add = new Button("Add");

		headerTable.setWidget(0, 0, day);
		headerTable.setWidget(0, 1, date);
		headerTable.setWidget(0, 2, startTime);
		headerTable.setWidget(0, 3, endTime);
		headerTable.setWidget(0, 4, add);

		RootPanel.get("container").add(mainPanel);

		setStyles();

		startTime.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				startTime.selectAll();
			}
		});

		endTime.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				// endTime.selectAll();
			}
		});

		entriesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contentPanel.clear();
				contentPanel.add(entriesPanel);

			}
		});
		datesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contentPanel.clear();
				contentPanel.add(datesPanel);
			}

		});
		exportButton.addClickHandler(new ClickHandler() {

			@Override
			public synchronized void onClick(ClickEvent event) {
				contentPanel.clear();
				contentPanel.add(exportPanel);
				exportText.setText("Exporting...");
				applicationService.getEntries(new Calendar(2000, 0, 0),
						new Calendar(2100, 12, 31), exportCallback);
			}
		});

		importButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				contentPanel.clear();
				contentPanel.add(importPanel);
				importText.setText("");
				importText.setFocus(true);
			}
		});

		importSubmit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				importSubmit.setEnabled(false);
				applicationService.importEntries(importText.getText(),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								importSubmit.setEnabled(true);
								importText.setText(caught.getMessage() + "/n"
										+ importText.getText());
							}

							@Override
							public void onSuccess(Void result) {
								importSubmit.setEnabled(true);
								importText.setText("imported succesfully");
							}
						});
			}
		});

		KeyDownHandler dateChanger = createDateChanger();
		startTime.addKeyDownHandler(dateChanger);
		endTime.addKeyDownHandler(dateChanger);
		startTime.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (!Character.isDigit(event.getCharCode()))
					startTime.cancelKey();
				else if (startTime.getText().length() > 3
						&& startTime.getSelectionLength() == 0) {
					startTime.cancelKey();
					endTime.setText("" + event.getCharCode());
					endTime.setSelectionRange(1, 0);
					endTime.setFocus(true);
				}
				updateUI();
			}
		});

		endTime.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				updateUI();
			}
		});

		add.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				add.setEnabled(false);
				Date d = getDate();
				final Calendar cal = createCalendar(d);
				final long startMs = getTime(startTime.getText());
				final long durationMs = getTime(endTime.getText())
						- getTime(startTime.getText());

				applicationService.addEntry(cal, startMs, durationMs, "",
						new AsyncCallback<Long>() {

							@Override
							public void onFailure(Throwable error) {
								setInformation(error.getMessage());
								updateUI();
							}

							@Override
							public void onSuccess(Long entryId) {

								FlexTable table = (FlexTable) tablePanel
										.getWidget(0);

								Entry entry = new Entry();
								entry.setId(entryId);
								entry.setCalendar(cal);
								entry.setStartTimeMs(startMs);
								entry.setDurationMs(durationMs);
								table.insertRow(0);
								setEntry(table, 0, entry);

								if (getTime(endTime.getText()) > 15 * 60 * 60 * 1000)
									nextWorkingDay();
								startTime.setText("");
								endTime.setText("");
								updateUI();
								startTime.setFocus(true);
							}
						});
			}
		});

		reportButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				reportPanel.clear();
				contentPanel.clear();

				if (startDate.getHighlightedDate() == null
						|| endDate.getHighlightedDate() == null) {
					reportPanel.clear();
					reportPanel.add(new Label("Please select dates"));
					contentPanel.add(reportPanel);
					return;
				}
				Calendar start = toCalendar(startDate.getValue());
				Calendar finish = toCalendar(endDate.getValue());
				applicationService.getEntries(start, finish,
						new AsyncCallback<Entry[]>() {

							@Override
							public void onFailure(Throwable caught) {
								setInformation(caught.getMessage());
							}

							@Override
							public void onSuccess(Entry[] result) {
								try {
									reportPanel.clear();
									reportPanel.add(new HTML(
											"<h2>Timesheet</h2>"));
									DateTimeFormat reportDateFormat = DateTimeFormat
											.getFormat("dd MMM yyyy");
									NumberFormat nf = NumberFormat
											.getFormat("00");
									Date lastDate = null;
									long dayTotal = 0;
									long total = 0;
									Label lastDuration = null;
									for (Entry entry : result) {
										HorizontalPanel panel = new HorizontalPanel();
										String dateString = nf.format(entry
												.getCalendar().getDay())
												+ "/"
												+ nf.format(entry.getCalendar()
														.getMonth())
												+ "/"
												+ entry.getCalendar().getYear();
										Date d = dateFormat.parse(dateString);
										Label dayLabel = new Label(dayFormat
												.format(d));
										dayLabel.setStyleName("reportDay");

										Label dateLabel = new Label(
												reportDateFormat.format(d));

										dateLabel.setStyleName("reportDate");

										if (d.equals(lastDate)) {
											dateLabel.setText("");
											dayLabel.setText("");
											dayTotal += entry.getDurationMs();
											if (lastDuration != null)
												lastDuration.setVisible(false);
										} else
											dayTotal = entry.getDurationMs();

										Label startTimeLabel = new Label(
												msToString(entry
														.getStartTimeMs()));
										startTimeLabel
												.setStyleName("reportStartTime");
										Label endTimeLabel = new Label(
												msToString(entry
														.getDurationMs()
														+ entry
																.getStartTimeMs()));
										endTimeLabel
												.setStyleName("reportEndTime");
										Label durationLabel = new Label(
												msToString(dayTotal));
										durationLabel
												.setStyleName("reportDuration");

										panel.add(dayLabel);
										panel.add(dateLabel);
										panel.add(startTimeLabel);
										panel.add(endTimeLabel);
										panel.add(durationLabel);
										reportPanel.add(panel);
										lastDate = d;
										total += entry.getDurationMs();
										lastDuration = durationLabel;
									}
									String gapStyle = " style=\"margin-bottom:4em\"";
									reportPanel.add(new HTML("<p><b>Total: "
											+ msToString(total) + "</b></p>"));
									reportPanel.add(new HTML("<p" + gapStyle
											+ ">Submitted by</p>"));
									reportPanel.add(new HTML("<p" + gapStyle
											+ ">Signature</p>"));
									reportPanel.add(new HTML("<p" + gapStyle
											+ ">Date</p>"));
									reportPanel.add(new HTML("<p" + gapStyle
											+ ">Authorized by</p>"));
									reportPanel.add(new HTML("<p" + gapStyle
											+ ">Signature</p>"));
									reportPanel.add(new HTML("<p" + gapStyle
											+ ">Date</p>"));

									reportPanel.setVisible(true);
								} catch (RuntimeException e) {
									Window.alert("an error occurred");
								}
							}
						});

				contentPanel.add(reportPanel);
			}
		});

		updateUI();
		setInformation(null);
		updateEntries();
		startTime.setFocus(true);
		contentPanel.clear();
		contentPanel.add(entriesPanel);
	}

	public Calendar toCalendar(Date d) {
		return new Calendar(Integer.parseInt(yearFormat.format(d)), Integer
				.parseInt(monthFormat.format(d)), Integer
				.parseInt(monthDayFormat.format(d)));
	}

	private void setDate(Date d) {
		date.setText(dateFormat.format(d));
		day.setText(dayFormat.format(d));
	}

	private void nextWorkingDay() {
		Date d = getDate();
		if (dayFormat.format(d).equals("Friday"))
			addDays(3);
		else
			addDays(1);
	}

	private void updateUI() {
		if (startTime.getText().length() == 4
				&& endTime.getText().length() >= 3)
			add.setEnabled(true);
		else
			add.setEnabled(false);
		if (!startTime.isEnabled())
			startTime.setEnabled(true);
		if (!endTime.isEnabled())
			endTime.setEnabled(true);
	}

	private long getTime(String s) {
		return (Integer.parseInt(s.substring(0, 2)) * 60 + Integer.parseInt(s
				.substring(2))) * 60 * 1000;
	}

	private KeyDownHandler createDateChanger() {
		return new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
					addDays(1);
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN)
					addDays(-1);
				else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updateUI();
					add.click();
				}
				updateUI();
			}

		};
	}

	private Date getDate() {
		Date t = dateFormat.parse(date.getText());
		return t;
	}

	private void addDays(long numDays) {
		Date t = dateFormat.parse(date.getText());
		// add an extra 2 hours to cope with daylight saving switchovers
		t = new Date(t.getTime() + (numDays * 24 + 2) * 3600 * 1000);
		setDate(t);
	}

	private String msToString(long ms) {
		long hours = ms / MILLIS_PER_HOUR;
		StringBuffer s = new StringBuffer();
		if (hours < 10)
			s.append("0");
		s.append(hours + ":");
		long minutes = (ms - hours * MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
		if (minutes < 10)
			s.append("0");
		s.append(minutes + "");
		return s.toString();
	}

	private class EntriesCallback implements AsyncCallback<Entry[]> {

		@Override
		public void onFailure(Throwable caught) {
			information.setText(caught.getMessage());
		}

		@Override
		public void onSuccess(Entry[] result) {
			tablePanel.clear();
			FlexTable table = new FlexTable();
			table.setStyleName("entries");
			int row = 0;

			for (int i = result.length - 1; i >= 0; i--) {
				Entry entry = result[i];
				setEntry(table, row, entry);
				row++;
			}
			tablePanel.add(table);
		}

	}

	private class ExportCallback implements AsyncCallback<Entry[]> {

		@Override
		public void onFailure(Throwable caught) {
			information.setText(caught.getMessage());
		}

		@Override
		public void onSuccess(Entry[] result) {
			StringBuffer exportString = new StringBuffer();
			for (Entry entry : result) {
				exportString.append(getExportLine(entry));
			}
			exportText.setText(exportString.toString());
			exportText.setVisible(true);
			exportText.setFocus(true);
		}

		private String getExportLine(Entry entry) {
			NumberFormat nf = NumberFormat.getFormat("00");
			String dateString = nf.format(entry.getCalendar().getDay()) + "/"
					+ nf.format(entry.getCalendar().getMonth()) + "/"
					+ entry.getCalendar().getYear();
			Date d = dateFormat.parse(dateString);
			StringBuffer exportLine = new StringBuffer();
			exportLine.append(shortDateFormat.format(d));
			exportLine.append("\t");
			exportLine.append(msToString(entry.getStartTimeMs()));
			exportLine.append("\t");
			exportLine.append(msToString(entry.getStartTimeMs()
					+ entry.getDurationMs()));
			exportLine.append("\n");
			return exportLine.toString();
		}
	}

	private void setEntry(final FlexTable table, int row, final Entry entry) {
		NumberFormat nf = NumberFormat.getFormat("00");
		String dateString = nf.format(entry.getCalendar().getDay()) + "/"
				+ nf.format(entry.getCalendar().getMonth()) + "/"
				+ entry.getCalendar().getYear();
		Date d = dateFormat.parse(dateString);
		table.setText(row, 0, dayFormat.format(d));
		table.setText(row, 1, dateString);
		table.setText(row, 2, msToString(entry.getStartTimeMs()));
		table.setText(row, 3, msToString(entry.getStartTimeMs()
				+ entry.getDurationMs()));
		final Button delete = new Button("Delete");
		delete.setStyleName("delete");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				applicationService.deleteEntry(entry.getId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								information.setText(caught.getMessage());
							}

							@Override
							public void onSuccess(Void result) {
								for (int i = 0; i < table.getRowCount(); i++) {
									if (table.getWidget(i, 4) == delete) {
										table.removeRow(i);
										return;
									}
								}
							}
						});
			}
		});
		table.setWidget(row, 4, delete);
	}

	private void setInformation(String s) {
		if (s == null)
			information.setVisible(false);
		else {
			information.setVisible(true);
			information.setText(s);
		}
	}

	private Calendar createCalendar(Date d) {
		int year = Integer.parseInt(yearFormat.format(d));
		int month = Integer.parseInt(monthFormat.format(d));
		int day = Integer.parseInt(monthDayFormat.format(d));
		return new Calendar(year, month, day);
	}

	private void updateEntries() {
		Date d1 = new Date(System.currentTimeMillis() - 3 * 31 * 24
				* MILLIS_PER_HOUR);
		Date d2 = new Date(System.currentTimeMillis() + 3 * 31 * 24
				* MILLIS_PER_HOUR);
		applicationService.getEntries(createCalendar(d1), createCalendar(d2),
				entriesCallback);
	}

	private void setStyles() {
		add.setStyleName("add");
		jobName.setStyleName("job");
		importButton.setStyleName("menuItem");
		exportButton.setStyleName("menuItem");
		reportButton.setStyleName("menuItem");
		datesButton.setStyleName("menuItem");
		entriesButton.setStyleName("menuItem");
		headerPanel.setStyleName("headerPanel");
		day.setStyleName("day");
		date.setStyleName("date");
		headerTable.setStyleName("entry");
		startDate.setStyleName("selectStartDate");
		endDate.setStyleName("selectEndDate");
		startTime.setStyleName("startTime");
		endTime.setStyleName("endTime");
		information.setStyleName("information");
		exportText.setStyleName("exportText");
		exportPanel.setStyleName("exportPanel");
		importPanel.setStyleName("importPanel");
		importSubmit.setStyleName("importSubmit");
		importText.setStyleName("importText");
		mainPanel.setStyleName("mainPanel");
		contentPanel.setStyleName("contentPanel");
		bodyPanel.setStyleName("bodyPanel");
		menuPanel.addStyleName("noprint");
		headerPanel.addStyleName("noprint");

	}
}
