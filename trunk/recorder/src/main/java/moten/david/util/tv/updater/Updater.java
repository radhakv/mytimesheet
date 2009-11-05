package moten.david.util.tv.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import moten.david.util.tv.Channel;
import moten.david.util.tv.ChannelsProvider;
import moten.david.util.tv.Configuration;
import moten.david.util.tv.DataFor;
import moten.david.util.tv.servlet.ApplicationInjector;

import com.google.inject.Inject;

public class Updater {

	private final Configuration configuration;
	private final ChannelsProvider stations;

	@Inject
	public Updater(Configuration configuration, ChannelsProvider stations) {
		this.configuration = configuration;
		this.stations = stations;
	}

	public void update(Set<String> stationIds) {
		updateDatalist();
		updateSchedules(stationIds);
	}

	private void updateSchedules(Set<String> stationIds) {
		for (Channel station : stations.getChannels())
			if (stationIds.contains(station.getId())) {
				System.out.println("found " + station.getId());
				for (DataFor dataFor : station.getDataFor()) {
					File scheduleFile = configuration.getProgrammeFile(station,
							dataFor.getDate());
					System.out.println("checking " + scheduleFile);
					if (!scheduleFile.exists()
							|| scheduleFile.lastModified() != dataFor
									.getLastModified().getTime())
						updateSchedule(scheduleFile, station, dataFor);
				}
			}
	}

	private void updateSchedule(File scheduleFile, Channel station,
			DataFor dataFor) {
		System.out.println("updating " + scheduleFile);
		try {
			FileOutputStream fos = new FileOutputStream(scheduleFile);
			URL url = configuration.getProgrammeUrl(station, dataFor.getDate());
			System.out.println(url);
			GZIPInputStream is = new GZIPInputStream(url.openStream());
			byte[] buf = new byte[1024]; // size can be
			int len;
			while ((len = is.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			is.close();
			fos.close();
			scheduleFile.setLastModified(dataFor.getLastModified().getTime());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.println("finished updating");

	}

	private void updateDatalist() {
		File datalist = configuration.getDataList();
		if (!datalist.exists()
				|| datalist.lastModified() < (System.currentTimeMillis() - 24 * 1000l * 3600))
			try {
				updateDataList(configuration.getDataList(), configuration
						.getDataListUrlZipped());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		else
			System.out.println("datalist up to date");
	}

	private void updateDataList(File outFile, String urlString)
			throws IOException {
		OutputStream out = new FileOutputStream(outFile);
		URL url = new URL(urlString);
		GZIPInputStream is = new GZIPInputStream(url.openStream());
		byte[] buf = new byte[1024]; // size can be
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		is.close();
		out.close();
	}

	public static void main(String[] args) {
		Set<String> channels = new HashSet<String>();
		channels.add("ABC2");
		channels.add("ABC-Can");
		channels.add("Prime-Can");
		channels.add("SBS-Can");
		channels.add("Ten-Can");
		channels.add("WIN-Can");
		channels.add("SBSTWO-NSW");
		channels.add("One-NSW");
		channels.add("GO");
		Updater updater = ApplicationInjector.getInjector().getInstance(
				Updater.class);
		updater.update(channels);
	}
}
