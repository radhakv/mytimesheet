package moten.david.time.mytime.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {

	public void addEntry(Calendar cal, long startTimeMs, long durationMs,
			String comment, AsyncCallback<Long> callback);

	public void getEntries(Calendar from, Calendar to,
			AsyncCallback<Entry[]> callback);

	public void deleteEntry(long entryId, AsyncCallback<Void> callback);

	public void importEntries(String s, AsyncCallback<Void> callback);
}
