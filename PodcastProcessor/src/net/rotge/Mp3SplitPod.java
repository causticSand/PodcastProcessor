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
	private static final String SPLITTIME = "14.0";
	private Path currentWorkingDirectory;

	public void split(File incFile, Path incDir) throws IOException {
		this.currentWorkingDirectory = incDir.toAbsolutePath();

		String[] mp3spltCommand = { "mp3splt", "-t", SPLITTIME, "-d", currentWorkingDirectory.toString(),
				incFile.toString() };

		ProcessBuilder probuilder = new ProcessBuilder(mp3spltCommand);
		probuilder.directory(new File("/usr/bin"));

		Process process = probuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			// System.out.println(line);
		}

		try {
			int exitValue = process.waitFor();
			System.out.println("ExitValue is " + exitValue);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		cleanUpSource(incFile.toPath());

	}

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

// Path path = Paths.get("data/subdir/logging-moved.properties");
//
// try {
// Files.delete(path);
// } catch (IOException e) {
// //deleting file failed
// e.printStackTrace();
// }
