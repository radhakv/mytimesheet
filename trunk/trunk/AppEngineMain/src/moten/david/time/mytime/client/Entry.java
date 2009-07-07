package moten.david.time.mytime.client;

import java.io.Serializable;

public class Entry implements Serializable {
	private long id;
	private Calendar calendar;
	private Long startTimeMs;
	private Long durationMs;
	private String comment;

	public Long getStartTimeMs() {
		return startTimeMs;
	}

	public void setStartTimeMs(Long startTimeMs) {
		this.startTimeMs = startTimeMs;
	}

	public Long getDurationMs() {
		return durationMs;
	}

	public void setDurationMs(Long durationMs) {
		this.durationMs = durationMs;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
}
