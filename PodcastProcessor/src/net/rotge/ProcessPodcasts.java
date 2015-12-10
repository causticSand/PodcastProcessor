package net.rotge;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * @author caustic
 * 
 */
public class ProcessPodcasts extends SimpleFileVisitor<Path> {

	private File currentWorkingDirectory;
	Path OUTDIR;
	Path start;
	long sizeOfMedium; // testing value

	public ProcessPodcasts(Path OUTDIR, Path start, long sizeOfMedium) {
		super();
		this.OUTDIR = OUTDIR;
		this.start = start;
		this.sizeOfMedium = sizeOfMedium;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
		if (attr.isRegularFile() && file.toString().contains(".mp3")) {

			// checks if the output directory size has not exceeded the pre-set size of medium
			// and writes an ISO file to OUTDIR directory
			if (FileUtils.sizeOfDirectory(OUTDIR.toFile()) > sizeOfMedium) {
				createIso(OUTDIR);
				eraseDisk();
				return FileVisitResult.TERMINATE;
			}
			if (file.toString().contains("converted_")) {
				return CONTINUE;
			}
			System.out.println("Current file being Processed: " + file.toString());
			File returnedProcessedFile = downSample(file.toFile(), currentWorkingDirectory.toPath());
			split(returnedProcessedFile, currentWorkingDirectory.toPath());

		} else {
			System.out.format("Skipping: %s \n", file);
		}
		return CONTINUE;
	}

	/*
	 * if the directory to write to doesn't exist, create it before going into it // and creates a directory named after
	 * the root directory from which the podcast // was originally pulled from. This is so all the directories from each
	 * separate // podcast remain in the same file structure as in the original. In other words, // keep podcasts in
	 * separate directories
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) {
		setCurrentWriteToDir(new File(OUTDIR + "/" + dir.toFile().getName()));

		if (!currentWorkingDirectory.exists()) {
			currentWorkingDirectory.mkdirs();
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return CONTINUE;
	}

	private void runOperatingSystemCommand(String[] command) throws IOException {
		ProcessBuilder proBuilder = new ProcessBuilder(command);
		Process proc = proBuilder.start();
		InputStream is = proc.getErrorStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		while ((line = br.readLine()) != null) {
			System.out.println(line + "\r");

		}
		try {
			int exitValue = proc.waitFor();
			System.out.println("ExitValue is " + exitValue);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}

		System.out.println(Arrays.toString(command));
	}

	/**
	 * Blanks the CDRW in the default CD/DVD drive
	 * 
	 * @throws IOException
	 */
	public void eraseDisk() throws IOException {

		// took 512 seconds to completely erase CDRW using blank=all
		// took 25 seconds for blank=fast
		// cdrecord -v -force blank=disk dev=/dev/cdrom
		final String PROGRAMTORUN = "/usr/bin/wodim";
		final String VERBOSEOUTPUT = "-v";
		final String FORCEERASE = "-force";
		final String DUMMYWRITE = "-dummy";
		final String LOCATIONOFCDRW = "/dev/sr0";
		final String BLANKTYPE = "-blank=fast";
		String[] eraseCDRWCommand = { PROGRAMTORUN, BLANKTYPE, LOCATIONOFCDRW };
		runOperatingSystemCommand(eraseCDRWCommand);
	}

	/**
	 * Looks in the directory where files have been written and creates an ISO image
	 * 
	 * @param rootIsoDirectory
	 * @throws IOException
	 */
	public void createIso(Path rootIsoDirectory) throws IOException {

		Date isoDate = new Date();
		SimpleDateFormat todayDate = new SimpleDateFormat("MM-dd");
		String dateToStr = todayDate.format(isoDate);
		String writeIsoToLocation = "/tmp/";
		String isoWritingProgram = "/usr/bin/genisoimage";
		String fileNameCase = "--allow-lowercase";
		String addRockRidge = "-R";
		String outLocation = "-o" + writeIsoToLocation;
		String isoNameDate = todayDate.format(isoDate);
		String isoPrefix = "Podcasts";
		String isoExtension = ".iso";

		String[] createIsoCommand = { isoWritingProgram, fileNameCase, addRockRidge,
				outLocation + isoPrefix + isoNameDate + isoExtension, rootIsoDirectory.toString() };
		System.out.println("i am here");
		runOperatingSystemCommand(createIsoCommand);

	}

	/**
	 * Takes a Path to write to and a File object and converts the file to a lower bitrate, lower sample rate, mono file
	 * then returns the File object of the newly created file
	 * 
	 * @param incFile
	 * @param directoryToWriteTo
	 * @return File object with a reference to the file that was just processed
	 * @throws IOException
	 */
	public File downSample(File incFile, Path directoryToWriteTo) throws IOException {
		String SAMPLE_RATE = "8";
		int NUM_CHANNELS = 1;
		String BIT_RATE = "-b 32";
		String encoderCommand = "/usr/bin/lame";
		String supressOutput = "-S";
		String downMixToMono = "-a";

		File fileInProgress;
		Path currentWorkingDirectory;
		Path source;

		fileInProgress = incFile;
		currentWorkingDirectory = directoryToWriteTo;

		String[] lameCommand = { encoderCommand, supressOutput, BIT_RATE, "--resample", SAMPLE_RATE, downMixToMono,
				incFile.toString(), "--out-dir", currentWorkingDirectory.toString() };
		runOperatingSystemCommand(lameCommand);
		source = incFile.toPath();
		Files.move(source, source.resolveSibling("converted_" + incFile.getName()));
		File fileJustConverted = new File(currentWorkingDirectory + "/" + fileInProgress.getName());
		return fileJustConverted;
	}
	// lame -b 32 --resample 8 -a sa.mp3 sa1.mp3

	/**
	 * Splits up the incomming file and writes it to the incDir path then deletes the non-split original file
	 * 
	 * @param incFile
	 * @param incDir
	 * @throws IOException
	 */
	public void split(File incFile, Path incDir) throws IOException {
		// String SPLITTIME = "-t 14.0";
		String SPLITNUMBER = "-S 3";
		String splitCommand = "/usr/bin/mp3splt";
		String outDirParameter = "-d";
		Path currentWorkingDirectory;
		currentWorkingDirectory = incDir.toAbsolutePath();

		String[] mp3spltCommand = { splitCommand, SPLITNUMBER, outDirParameter, currentWorkingDirectory.toString(),
				incFile.toString() };
		runOperatingSystemCommand(mp3spltCommand);
		cleanUpSource(incFile.toPath());
	}

	// TODO increase volume to the maximum
	/**
	 * Takes a file object and normalized the volume then writes the result to the outDir
	 * 
	 * @param incFile
	 * @param outDir
	 * @throws IOException
	 */
	public void equalizeVolume(File incFile, Path outDir) throws IOException {
		String fileToNormalize;
		String directoryToWriteTo;
		String normalizeCommand = "/usr/bin/mp3gain";
		String preventClipping = "-k";
		String setDefaultVolTo89db = "-r";
		fileToNormalize = incFile.getAbsolutePath();
		// using -k option auto lower's gain to prevent clipping
		// the -r option sets the default volume to 89db
		String[] createNormalizeCommand = { normalizeCommand, preventClipping, setDefaultVolTo89db, fileToNormalize };
		runOperatingSystemCommand(createNormalizeCommand);
	}

	// TODO determine the right size to not delete the original. If the original has less than
	// TODO two splits, the split method will fail.
	// This sub-method deletes the original file that was split
	/**
	 * Helper application that will delete the file sent to it. Works with the split() method
	 * 
	 * @param fileToDelete
	 */
	private void cleanUpSource(Path fileToDelete) {
		try {

				System.out.println("Deleting: " + Paths.get(fileToDelete.toString()));
				Files.delete(Paths.get(fileToDelete.toString()));

		} catch (IOException e) {
			System.out.println("Problem deleting source file in cleanUpSource");
			e.printStackTrace();
		}
	}

	// sets the directory where the output will be written to by working with the
	// preVisitDirectory() method
	private void setCurrentWriteToDir(File inDir) {
		this.currentWorkingDirectory = inDir;
	}
}
