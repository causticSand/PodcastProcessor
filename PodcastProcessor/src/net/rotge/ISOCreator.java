package net.rotge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISOCreator {

	private static Date isoDate = new Date();
	private static SimpleDateFormat todayDate = new SimpleDateFormat("MM-dd");
	private static String dateToStr = todayDate.format(isoDate);
	private static String writeIsoToLocation = "/tmp/";

	public void createIso(Path rootIsoDirectory) throws IOException {

		String[] createIsoCommand = { "/usr/bin/mkisofs", "--allow-lowercase", "-R", "-o",
				writeIsoToLocation + "Podcasts" + dateToStr + ".iso", rootIsoDirectory.toString() };
		ProcessBuilder probuilder = new ProcessBuilder(createIsoCommand);
		Process process = probuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			// uncomment this line to see ISO creation spam.
			// System.out.println(line);

		}

		try {
			int exitValue = process.waitFor();
			System.out.println("ExitValue is " + exitValue);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("ouput directory should be: " + writeIsoToLocation + "Podcasts" + dateToStr + ".iso");
	}

}

// 15:47:27 caustic@vega:/tmp$ mkisofs --allow-lowercase -R -o podcastISO.iso /tmp/output
