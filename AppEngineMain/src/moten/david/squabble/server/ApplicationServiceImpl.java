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
import moten.david.squabble.client.MyWord;

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

	public String newGame() {
		return System.currentTimeMillis() + "";
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

	public MyWord[] getWords(String gameId) {

		EntityManager em = emf.createEntityManager();
		List<Word> list = em.createQuery(
				"select * from moten.david.squabble.Word where gameId=:gameId")
				.getResultList();
		List<MyWord> words = new ArrayList<MyWord>();
		for (Word w : list) {
			MyWord word = new MyWord();
			word.setId(w.getId().getId());
			word.setBecameWord((w.getBecameWord() == null ? null : w
					.getBecameWord().getId()));
			word.setGameId(w.getGameId());
			word.setOwner(w.getOwner());
			word.setTimeBecameWord(w.getTimeBecameWord());
			word.setTimeCreated(w.getTimeCreated());
			word.setTimeVisible(w.getTimeVisible());
			word.setValue(w.getValue());
			word.setVisible(w.isVisible());
			words.add(word);
		}
		em.close();
		return words.toArray(new MyWord[] {});
	}
}