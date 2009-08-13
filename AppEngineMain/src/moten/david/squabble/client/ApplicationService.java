package moten.david.squabble.client;

import moten.david.squabble.Word;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {
	public Word[] getWords(String gameId);
}
