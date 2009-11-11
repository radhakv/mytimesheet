package moten.david.util.tv.ui.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {

	MyProgrammeItem[] getProgramme(String channel, Date date);
}
