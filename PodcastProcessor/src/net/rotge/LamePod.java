package net.rotge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class LamePod {
	public final static String SAMPLE_RATE = "--resample 8";
	public final static int NUM_CHANNELS = 1;
	public final static String BIT_RATE = "-b 32";
	private static String encoderCommand = "/usr/bin/lame";
	private static String supressOutput = "-quiet";
	private static String downMixToMono = "-a";

	private File fileInProgress;
	private Path currentWorkingDirectory;
	private Path source;

	public File downSample(File incFile, Path directoryToWriteTo) throws IOException {
		this.fileInProgress = incFile;
		this.currentWorkingDirectory = directoryToWriteTo;

		String[] lameCommand = { encoderCommand, supressOutput, BIT_RATE, SAMPLE_RATE, downMixToMono,
				incFile.toString(), "--out-dir", currentWorkingDirectory.toString() };

		ProcessBuilder probuilder = new ProcessBuilder(lameCommand);
		Process process = probuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		System.out.println("Converting: " + incFile.toString());
		while ((line = br.readLine()) != null) {
			// Uncomment to see downsampling output on command line
			// System.out.println(line);
		}

		try {
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				System.out.println("Sucess!");
			} else {
			System.out.println("ExitValue is " + exitValue);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// changes the name of the original file after it has been converted
		source = incFile.toPath();
		Files.move(source, source.resolveSibling("converted_" + incFile.getName()));
		File fileJustConverted = new File(currentWorkingDirectory + "/" + fileInProgress.getName());
		return fileJustConverted;
	}
	// lame -b 32 --resample 8 -a sa.mp3 sa1.mp3

}
