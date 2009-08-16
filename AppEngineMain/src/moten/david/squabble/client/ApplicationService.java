package moten.david.squabble.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {
	public MyWord[] getWords(String gameId);

	public String newGame();
}
