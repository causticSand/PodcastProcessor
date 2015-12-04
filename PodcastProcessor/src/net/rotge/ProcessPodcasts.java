package net.rotge;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author caustic
 * 
 */
public class ProcessPodcasts extends SimpleFileVisitor<Path> {
	public static final Path OUTDIR = Paths.get("/tmp/output");
	// public final static String rootDir = "/media/caustic/toshiba/podcasts/2015";
	// public final static Path start = Paths.get("/media/caustic/toshiba/podcasts/2015");
	// public final static Path start = Paths.get("/tmp/root");
	public final static Path start = Paths.get("/media/caustic/toshiba/podcasts/2015/Secular Media Network");
	final static long sizeOfMedium = 734003200; // this is 700mb size of CDRW in bytes
	// final static long sizeOfMedium = 30408704; // testing value
	private File currentWorkingDirectory;

	public static void main(String[] args) throws IOException {
		Files.walkFileTree(start, new ProcessPodcasts());
		CDRWBurner wipe = new CDRWBurner();
		wipe.eraseDisk();
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
		if (attr.isRegularFile() && file.toString().contains(".mp3")) {

			// checks if the output directory size has not exceeded the pre-set size of medium
			// and writes an ISO file to OUTDIR directory
			if (org.apache.commons.io.FileUtils.sizeOfDirectory(OUTDIR.toFile()) > sizeOfMedium) {
				ISOCreator ic = new ISOCreator();
				ic.createIso(OUTDIR);
				return FileVisitResult.TERMINATE;
			}
			// creates a LamePod object and using the current file, uses the downSample()
			// method to process it
			LamePod lp = new LamePod();
			System.out.println("Current file being Processed: " + file.toString());
			File returnedProcessedFile = lp.downSample(file.toFile(), currentWorkingDirectory.toPath());

			// calls the PodcastNormalizer object to normalize each mp3 and write it to
			// output directory
			PodcastNormalizer pn = new PodcastNormalizer();
			pn.equalizeVolume(returnedProcessedFile, currentWorkingDirectory.toPath());

			// split the mp3's into time defined in Mp3SplitPod class
			Mp3SplitPod sp = new Mp3SplitPod();
			sp.split(returnedProcessedFile, currentWorkingDirectory.toPath());

		} else {
			System.out.format("Skipping: %s ", file);
		}
		return CONTINUE;
	}

	// if the directory to write to doesn't exist, create it before going into it
	// and creates a directory named after the root directory from which the podcast
	// was originally pulled from. This is so all the directories from each separate
	// podcast remain in the same file structure as in the original. In other words,
	// keep podcasts in separate directories
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

	// sets the directory where the output will be written to by working with the
	// preVisitDirectory() method
	private void setCurrentWriteToDir(File inDir) {
		this.currentWorkingDirectory = inDir;
	}
}
