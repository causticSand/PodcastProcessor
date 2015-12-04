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
	private static String isoWritingProgram = "/usr/bin/mkisofs";
	private static String fileNameCase = "--allow-lowercase";
	private static String addRockRidge = "-R";
	private static String outLocation = "-o " + writeIsoToLocation;
	private static String isoNameDate = todayDate.format(isoDate);
	private static String isoPrefix = "Podcasts";
	private static String isoExtension = ".iso";

	/**
	 * Takes a Path object and makes an ISO file in static location
	 * 
	 * @param rootIsoDirectory
	 * @throws IOException
	 */
	public void createIso(Path rootIsoDirectory) throws IOException {

		//
		String[] createIsoCommand = { isoWritingProgram, fileNameCase, addRockRidge,
				outLocation + isoPrefix + isoNameDate + isoExtension, rootIsoDirectory.toString() };
		ProcessBuilder probuilder = new ProcessBuilder(createIsoCommand);
		Process process = probuilder.start();

		InputStream is = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		System.out.println("Creating ISO file: " + outLocation + isoPrefix + isoNameDate + isoExtension);
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

	}

}
