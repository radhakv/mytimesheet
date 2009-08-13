package moten.david.squabble.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {
	public void getWords(String gameId, AsyncCallback<String[]> callback);
}
