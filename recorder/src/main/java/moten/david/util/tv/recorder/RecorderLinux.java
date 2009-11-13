package moten.david.util.tv.recorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import moten.david.util.tv.Configuration;
import moten.david.util.tv.schedule.ScheduleItem;
import moten.david.util.tv.servlet.ApplicationInjector;

import com.google.inject.Inject;

public class RecorderLinux implements Recorder {
	private static Logger log = Logger.getLogger(RecorderLinux.class.getName());
	private final File recordings;
	private final AliasProvider aliasProvider;

	@Inject
	public RecorderLinux(Configuration configuration,
			AliasProvider aliasProvider) {
		this.aliasProvider = aliasProvider;
		recordings = configuration.getRecordingsFolder();
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
		command.add("dvb://" + aliasProvider.getAlias(item.getChannel()));
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

	@Override
	public void play(String channelId) {
		final String alias = aliasProvider.getAlias(channelId);
		List<String> command = new ArrayList<String>() {
			{
				add("/usr/bin/mplayer");
				add("dvb://" + alias);
			}
		};
		;
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		try {
			Process process = builder.start();
			new ConsoleWriter(process.getInputStream());
			int resultCode = process.waitFor();
			log.info("result code = " + resultCode);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static class ConsoleWriter {

		public ConsoleWriter(final InputStream is) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					String line;
					try {
						while ((line = br.readLine()) != null)
							log.info(line);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}
}
