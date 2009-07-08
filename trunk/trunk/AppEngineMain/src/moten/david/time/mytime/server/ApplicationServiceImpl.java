package moten.david.time.mytime.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import moten.david.time.mytime.ApplicationInjector;
import moten.david.time.mytime.Entry;
import moten.david.time.mytime.User;
import moten.david.time.mytime.client.ApplicationService;
import moten.david.time.mytime.client.Calendar;

import org.junit.Test;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
		ApplicationService {

	private static final long serialVersionUID = -5548669843805840967L;

	private static final long MILLIS_PER_MINUTE = 60 * 1000;
	private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

	private static Logger log = Logger.getLogger(ApplicationServiceImpl.class
			.getName());
	@Inject
	private EntityManagerFactory emf;
	private UserService userService;

	public ApplicationServiceImpl() {
		ApplicationInjector.getInjector().injectMembers(this);
		userService = UserServiceFactory.getUserService();

	}

	private com.google.appengine.api.users.User getGoogleUser() {
		com.google.appengine.api.users.User googleUser = userService
				.getCurrentUser();
		if (googleUser == null)
			throw new RuntimeException("not logged in");
		else
			return googleUser;
	}

	private double toJulianDate(int year, int month, int day, int hour,
			int minute) {
		GregorianCalendar cal = new GregorianCalendar(TimeZone
				.getTimeZone("GMT"));
		cal.set(java.util.Calendar.YEAR, year);
		cal.set(java.util.Calendar.MONTH, month - 1);
		cal.set(java.util.Calendar.DAY_OF_MONTH, day);
		cal.set(java.util.Calendar.HOUR, hour);
		cal.set(java.util.Calendar.MINUTE, minute);
		return Time.getJulianDayNumber(cal);
	}

	@Override
	public Long addEntry(Calendar cal, long startTimeMs, long durationMs,
			String comment) {
		EntityManager em = null;
		try {
			log.info("adding entry for " + cal);
			com.google.appengine.api.users.User googleUser = getGoogleUser();
			{
				em = emf.createEntityManager();
				em.getTransaction().begin();
				Entry entry = new Entry();
				int hour = (int) (startTimeMs / MILLIS_PER_HOUR);
				int minute = (int) ((startTimeMs - hour * MILLIS_PER_HOUR) / MILLIS_PER_MINUTE);
				java.util.Calendar calendar = new GregorianCalendar(TimeZone
						.getTimeZone("UTC"));
				calendar.set(java.util.Calendar.YEAR, cal.getYear());
				calendar.set(java.util.Calendar.MONTH, cal.getMonth() - 1);
				calendar.set(java.util.Calendar.DAY_OF_MONTH, cal.getDay());
				calendar.set(java.util.Calendar.HOUR_OF_DAY, hour);
				calendar.set(java.util.Calendar.MINUTE, minute);
				entry.setStartTime(calendar.getTime());
				entry.setDurationMs(durationMs);
				entry.setComment(comment);
				entry.setJobId(1l);
				entry.setUsername(googleUser.getEmail());
				em.persist(entry);
				em.getTransaction().commit();
				log.info("new entry id=" + entry.getId());
				long entryId = entry.getId().getId();
				return entryId;
			}
		} catch (RuntimeException e) {
			log.throwing(getClass().getName(), "add", e);
			if (em != null && em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw e;
		} finally {
			if (em != null && em.isOpen())
				em.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public moten.david.time.mytime.client.Entry[] getEntries(Calendar from,
			Calendar to) {
		log.info("getting entries");
		try {
			com.google.appengine.api.users.User googleUser = getGoogleUser();

			ArrayList<moten.david.time.mytime.client.Entry> entries = new ArrayList<moten.david.time.mytime.client.Entry>();
			{
				EntityManager em = emf.createEntityManager();
				em.getTransaction().begin();
				java.util.Calendar fromCalendar = new GregorianCalendar(
						TimeZone.getTimeZone("GMT"));
				fromCalendar.set(java.util.Calendar.YEAR, from.getYear());
				fromCalendar.set(java.util.Calendar.MONTH, from.getMonth() - 1);
				fromCalendar
						.set(java.util.Calendar.DAY_OF_MONTH, from.getDay());
				java.util.Calendar toCalendar = new GregorianCalendar(TimeZone
						.getTimeZone("GMT"));
				toCalendar.set(java.util.Calendar.YEAR, to.getYear());
				toCalendar.set(java.util.Calendar.MONTH, to.getMonth() - 1);
				toCalendar.set(java.util.Calendar.DAY_OF_MONTH, to.getDay());

				List<Entry> list = em
						.createQuery(
								"select e from "
										+ Entry.class.getName()
										+ " e where username=:username and jobId=:jobId and startTime>=:fromDate and startTime<=:toDate")
						.setParameter("username", googleUser.getEmail())
						.setParameter("fromDate", fromCalendar.getTime())
						.setParameter("toDate", toCalendar.getTime())
						.setParameter("jobId", 1l).getResultList();
				for (Entry entry : list)
					entries.add(createEntry(entry));
				em.getTransaction().commit();
				sortEntries(entries);
				em.close();
			}
			log.info("returning " + entries.size() + " entries");
			return entries
					.toArray(new moten.david.time.mytime.client.Entry[] {});
		} catch (RuntimeException e) {
			log.throwing(getClass().getName(), "getEntries", e);
			throw e;
		}
	}

	private void sortEntries(
			ArrayList<moten.david.time.mytime.client.Entry> entries) {
		Collections.sort(entries,
				new Comparator<moten.david.time.mytime.client.Entry>() {

					@Override
					public int compare(moten.david.time.mytime.client.Entry e1,
							moten.david.time.mytime.client.Entry e2) {

						int n = compare(e1.getCalendar().getYear(), e2
								.getCalendar().getYear());
						if (n != 0)
							return n;
						n = compare(e1.getCalendar().getMonth(), e2
								.getCalendar().getMonth());
						if (n != 0)
							return n;
						n = compare(e1.getCalendar().getDay(), e2.getCalendar()
								.getDay());
						if (n != 0)
							return n;
						return e1.getStartTimeMs().compareTo(
								e2.getStartTimeMs());
					}

					private int compare(int i1, int i2) {
						return ((Integer) i1).compareTo(i2);
					}
				});

	}

	private User getUser(EntityManager em) {
		com.google.appengine.api.users.User googleUser = getGoogleUser();
		List<User> users = em
				.createQuery(
						"select u from moten.david.time.mytime.User u where u.username=:username")
				.setParameter("username", googleUser.getEmail())
				.getResultList();
		if (users.size() == 0)
			return null;
		else
			return users.get(0);
	}

	private moten.david.time.mytime.client.Entry createEntry(Entry entry) {
		moten.david.time.mytime.client.Entry e = new moten.david.time.mytime.client.Entry();
		e.setId(entry.getId().getId());
		java.util.Calendar cal = new GregorianCalendar(TimeZone
				.getTimeZone("GMT"));
		cal.setTime(entry.getStartTime());
		Calendar calendar = new Calendar();
		calendar.setYear(cal.get(java.util.Calendar.YEAR));
		calendar.setMonth(cal.get(java.util.Calendar.MONTH) + 1);
		calendar.setDay(cal.get(java.util.Calendar.DAY_OF_MONTH));
		int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
		int minute = cal.get(java.util.Calendar.MINUTE);
		e.setCalendar(calendar);
		e.setComment(entry.getComment());
		e.setStartTimeMs(hour * MILLIS_PER_HOUR + minute * MILLIS_PER_MINUTE);
		e.setDurationMs(entry.getDurationMs());
		return e;
	}

	@Override
	public void deleteEntry(long id) {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			log.info("deleting entry " + id);
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();
			{
				Entry entry = em.find(Entry.class, id);
				em.remove(entry);
			}
			tx.commit();
			log.info("committed");
		} catch (RuntimeException e) {
			log.throwing(getClass().getName(), "deleteEntry", e);
			throw e;
		} finally {
			if (tx != null && tx.isActive())
				tx.rollback();
			if (em != null && em.isOpen())
				em.close();
		}
	}

	private long getMs(String time) {
		long result = 0;
		result += Long.parseLong(time.substring(0, 2)) * 60 * 60 * 1000;
		result += Long.parseLong(time.substring(3, 5)) * 60 * 1000;
		return result;
	}

	@Override
	public void importEntries(String s) {
		try {

			ByteArrayInputStream bytes = new ByteArrayInputStream(s.getBytes());
			InputStreamReader isr = new InputStreamReader(bytes);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				String[] items = line.split("\t");
				// dd/MM/yy
				String date = items[0];
				String startTime = items[1];
				String endTime = items[2];
				moten.david.time.mytime.client.Entry entry = new moten.david.time.mytime.client.Entry();

				Calendar cal = new Calendar();
				cal.setYear(2000 + Integer.parseInt(date.substring(6)));
				cal.setMonth(Integer.parseInt(date.substring(3, 5)));
				cal.setDay(Integer.parseInt(date.substring(0, 2)));
				entry.setStartTimeMs(getMs(startTime));
				entry.setDurationMs(getMs(endTime) - getMs(startTime));
				addEntry(cal, entry.getStartTimeMs(), entry.getDurationMs(),
						null);
			}
			br.close();
			isr.close();
			bytes.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	private void test() {

	}

}
