package moten.david.time.mytime.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {

	/**
	 * 
	 * @param cal
	 * @param startTimeMs
	 * @param durationMs
	 * @param comment
	 * @return entryId
	 */
	public Long addEntry(Calendar cal, long startTimeMs, long durationMs,
			String comment);

	public Entry[] getEntries(Calendar from, Calendar to);

	public void deleteEntry(long id);

	public void importEntries(String s);
}
