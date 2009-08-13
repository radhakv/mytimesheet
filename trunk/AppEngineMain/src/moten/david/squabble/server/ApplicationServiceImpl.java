package moten.david.squabble.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import moten.david.squabble.ApplicationInjector;
import moten.david.squabble.Word;
import moten.david.squabble.client.ApplicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
		ApplicationService {

	private static final long serialVersionUID = 3837567586122697633L;
	@Inject
	private EntityManagerFactory emf;
	private SquabbleUtil squabbleUtil;

	public ApplicationServiceImpl() {
		ApplicationInjector.getInjector().injectMembers(this);
	}

	public String newGame(String gameName, String language) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String prefix = df.format(new Date());
		String gameId = prefix + " " + gameName;
		createGame(gameId, language);
		return gameId;
	}

	private void createGame(String gameId, String language) {
		for (String letter : squabbleUtil.getLetters(language)) {
			EntityManager em = emf.createEntityManager();
			Word w = new Word();
			w.setGameId(gameId);
			w.setValue(letter);
			w.setVisible(false);
			w.setTimeCreated(new Date());
			em.persist(w);
			em.close();
		}
	}

	public String[] getGames(boolean activeOnly, Date since) {
		return null;
	}

	public synchronized boolean processGuess(String player, String word) {
		return true;
	}

	public synchronized void processNextTurn(String player) {

	}

	public Word[] getWords(String gameId) {
		List<Word> words = new ArrayList<Word>();
		Word word = new Word();
		word.setValue("hello");
		word.setOwner("dave");

		return words.toArray(new Word[] {});
	}

}
