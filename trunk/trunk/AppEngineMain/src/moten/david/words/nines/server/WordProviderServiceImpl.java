package moten.david.words.nines.server;

import java.util.logging.Logger;

import moten.david.words.nines.ApplicationInjector;
import moten.david.words.nines.WordProvider;
import moten.david.words.nines.client.WordProviderService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WordProviderServiceImpl extends RemoteServiceServlet implements
		WordProviderService {
	private static Logger log = Logger.getLogger(WordProviderServiceImpl.class
			.getName());
	@Inject
	private WordProvider wordProvider;

	public WordProviderServiceImpl() {
		ApplicationInjector.getInjector().injectMembers(this);
	}

	public String getRandomWord() {
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		log.info("serverInfo=" + serverInfo);
		log.info("userAgent=" + userAgent);
		return wordProvider.nextRandomWord();
	}

}
