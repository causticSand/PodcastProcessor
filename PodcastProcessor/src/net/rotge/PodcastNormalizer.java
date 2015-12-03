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

	public void split(File incFile, Path outDir) throws IOException {
		fileToNormalize = incFile.getAbsolutePath();

		String[] createNormalizeCommand = { "mp3gain", "-k", "-r", fileToNormalize };
		ProcessBuilder proBuilder = new ProcessBuilder(createNormalizeCommand);
		Process process = proBuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		try {
			int exitValue = process.waitFor();
			System.out.println("\n\nExitValue is " + exitValue);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Normalizing: " + fileToNormalize);
	}

}
