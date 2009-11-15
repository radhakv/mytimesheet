package moten.david.util.tv.ui.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import moten.david.util.tv.Channel;
import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Util;
import moten.david.util.tv.programme.Programme;
import moten.david.util.tv.programme.ProgrammeItem;
import moten.david.util.tv.programme.ProgrammeProvider;
import moten.david.util.tv.recorder.Recorder;
import moten.david.util.tv.servlet.ApplicationInjector;
import moten.david.util.tv.ui.client.ApplicationService;
import moten.david.util.tv.ui.client.MyProgrammeItem;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
		ApplicationService {

	private static Logger log = Logger.getLogger(ApplicationServiceImpl.class
			.getName());

	@Inject
	private ProgrammeProvider programmeProvider;
	@Inject
	private ChannelsProvider channelsProvider;
	@Inject
	private Recorder recorder;

	private final Channel[] allChannels;

	public ApplicationServiceImpl() {
		log.info("injecting members");
		ApplicationInjector.getInjector().injectMembers(this);
		log.info("getting channels");
		allChannels = channelsProvider.getChannels();
		log.info("constructed");
	}

	@Override
	public MyProgrammeItem[] getProgramme(String[] channelIds, Date start,
			Date finish) {
		ArrayList<MyProgrammeItem> list = new ArrayList<MyProgrammeItem>();
		for (String channelId : channelIds) {
			log.info("getting programme for channel " + channelId);
			Channel channel = Util.getChannel(channelId, allChannels);
			Programme items = programmeProvider.getProgramme(channel, start);
			for (ProgrammeItem item : items) {
				MyProgrammeItem p = new MyProgrammeItem();
				p.setChannelId(item.getChannelId());
				p.setDescription(item.getDescription());
				p.setStart(item.getStart());
				p.setStop(item.getStop());
				p.setSubTitle(item.getSubTitle());
				p.setTitle(item.getTitle());
				p.setStartTimeInMinutes(getTimeInMinutes(item.getStart()));
				p.setStopTimeInMinutes(getTimeInMinutes(item.getStop()));
				list.add(p);
			}
		}
		log.info("obtained programme");
		return list.toArray(new MyProgrammeItem[] {});
	}

	private int getTimeInMinutes(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		return hours * 60 + minutes;
	}

	@Override
	public void play(String channelId) {
		recorder.play(channelId);
	}
}
