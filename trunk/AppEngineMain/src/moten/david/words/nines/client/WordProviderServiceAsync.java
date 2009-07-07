package moten.david.words.nines.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>WordProviderService</code>.
 */
public interface WordProviderServiceAsync {
	void getRandomWord(AsyncCallback<String> callback);
}
