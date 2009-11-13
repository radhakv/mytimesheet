package moten.david.util.tv.ui.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {
	void getProgramme(String channelId, Date date,
			AsyncCallback<MyProgrammeItem[]> callback);

	void play(String channelId, AsyncCallback<Void> callback);
}
