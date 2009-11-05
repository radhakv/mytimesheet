package moten.david.util.tv.recorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import moten.david.util.tv.Configuration;
import moten.david.util.tv.schedule.ScheduleItem;
import moten.david.util.tv.servlet.ApplicationInjector;

import com.google.inject.Inject;

public class RecorderLinux implements Recorder {

	private final File recordings;
	private final HashMap<String, String> mplayerChannels;

	@Inject
	public RecorderLinux(Configuration configuration) {
		recordings = configuration.getRecordingsFolder();
		mplayerChannels = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream("/channels.txt")));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					String[] items = line.split("\t");
					mplayerChannels.put(items[0], items[1]);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public boolean isRecording(ScheduleItem item) {
		return false;
	}

	@Override
	public void startRecording(ScheduleItem item) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
		List<String> command = new ArrayList<String>();
		command.add("/usr/bin/mplayer");
		command.add("-dumpstream");
		command.add("-dumpfile");
		String filename = df.format(item.getStartDate()) + "_"
				+ item.getChannel() + "_" + item.getName() + ".avi";
		command.add(new File(recordings, filename).getAbsolutePath());
		command.add("dvb://" + mplayerChannels.get(item.getChannel()));
		ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stopRecording(ScheduleItem item) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		ScheduleItem item = new ScheduleItem("test", "ABC2", new Date(),
				new Date());
		Recorder recorder = ApplicationInjector.getInjector().getInstance(
				Recorder.class);
		recorder.startRecording(item);
	}
}
