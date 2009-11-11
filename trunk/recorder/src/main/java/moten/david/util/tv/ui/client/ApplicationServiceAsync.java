package moten.david.util.tv.ui.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {
	void getProgramme(String channel, Date date,
			AsyncCallback<MyProgrammeItem[]> callback);
}
