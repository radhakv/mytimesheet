package moten.david.music.album.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Player {

	private Process process;
	private ArrayList<File> list;

	public void play(File directory, boolean randomize) {
		stop();

		list = new ArrayList<File>();

		for (File f : directory.listFiles())
			list.add(f);
		if (randomize)
			Collections.shuffle(list);
		playList(list);

	}

	private void playList(ArrayList<File> list2) {
		StringBuffer s = new StringBuffer();
		for (File f : list)
			if (Util.isAudio(f))
				s.append(f.getAbsolutePath() + "\n");
		try {
			File playlist = File.createTempFile("albumViewer-", ".playlist");
			FileOutputStream fos = new FileOutputStream(playlist);
			fos.write(s.toString().getBytes());
			fos.close();
			String[] command = new String[] { "mplayer", "-quiet", "-playlist",
					playlist.getAbsolutePath() };
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream();
			process = builder.start();
			log(process.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void write(byte[] s) throws IOException {
		if (process != null) {
			process.getOutputStream().write(s);
			process.getOutputStream().flush();
		}
	}

	public void forward() {
		synchronized (this) {
			if (list != null) {
				File f = list.remove(0);
				list.add(f);
				stop();
				playList(list);
			}
		}
	}

	public void previous() {
		synchronized (this) {
			if (list != null) {
				File f = list.remove(list.size() - 1);
				list.add(0, f);
				stop();
				playList(list);
			}
		}
	}

	private void log(final InputStream is) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				try {
					while ((line = br.readLine()) != null) {
						System.out.println(line);
					}
					br.close();
					isr.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	public void stop() {
		if (process != null)
			process.destroy();

	}

}
