package net.rotge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class PodcastNormalizer {
	String fileToNormalize;
	String directoryToWriteTo;
	private static String normalizeCommand = "/usr/bin/mp3gain";
	private static String preventClipping = "-k";
	private static String setDefaultVolTo89db = "-r";

	public void equalizeVolume(File incFile, Path outDir) throws IOException {
		fileToNormalize = incFile.getAbsolutePath();

		// using -k option auto lower's gain to prevent clipping
		// the -r option sets the default volume to 89db
		String[] createNormalizeCommand = { normalizeCommand, preventClipping, setDefaultVolTo89db, fileToNormalize };
		ProcessBuilder proBuilder = new ProcessBuilder(createNormalizeCommand);
		Process process = proBuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		System.out.println("Normalizing: " + fileToNormalize);
		while ((line = br.readLine()) != null) {
			// uncomment the below line to show console output
			// System.out.println(line);
		}

		try {
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				System.out.println("Sucess!");
			} else {
				System.out.println("\nExitValue is " + exitValue);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

}
