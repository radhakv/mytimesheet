package moten.david.util.tv.programme;

import java.util.Date;

import moten.david.util.tv.Channel;

public interface ProgrammeProvider {

	Channel[] getStations();

	Programme getSchedule(Channel station, Date date);

}
