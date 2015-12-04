package net.rotge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Mp3SplitPod {

	final static long sizeOfMedium = 36700160;
	private static final String SPLITTIME = " -t 14.0";
	private static String splitCommand = "/usr/bin/mp3splt";
	private static String outDirParameter = "-d";
	private Path currentWorkingDirectory;

	public void split(File incFile, Path incDir) throws IOException {
		this.currentWorkingDirectory = incDir.toAbsolutePath();

		String[] mp3spltCommand = { splitCommand, SPLITTIME, outDirParameter, currentWorkingDirectory.toString(),
				incFile.toString() };

		ProcessBuilder probuilder = new ProcessBuilder(mp3spltCommand);
		Process process = probuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		System.out.println("Splitting file: " + incFile.toString());
		while ((line = br.readLine()) != null) {
			// uncomment for console output
			// System.out.println(line);
		}
		try {
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				System.out.println("Success!");
			} else {
				System.out.println("ExitValue is " + exitValue);
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		cleanUpSource(incFile.toPath());
	}

	// This sub-method deletes the original file that was split
	private void cleanUpSource(Path fileToDelete) {
		try {
			System.out.println("Deleting: " + Paths.get(fileToDelete.toString()));
			Files.delete(Paths.get(fileToDelete.toString()));
		} catch (IOException e) {
			System.out.println("Problem deleting source file in MP3SPLT");
			e.printStackTrace();
		}
	}
}
