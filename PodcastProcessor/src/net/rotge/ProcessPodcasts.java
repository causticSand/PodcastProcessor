package net.rotge;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ProcessPodcasts extends SimpleFileVisitor<Path> {
	public static final Path OUTDIR = Paths.get("/tmp/output");
	// public final static String rootDir = "/media/caustic/toshiba/podcasts/2015";
	// public final static Path start = Paths.get("/media/caustic/toshiba/podcasts/2015");
	// public final static Path start = Paths.get("/tmp/root");
	public final static Path start = Paths.get("/media/caustic/toshiba/podcasts/2015/Secular Media Network");
	final static long sizeOfMedium = 734003200;
	// final static long sizeOfMedium = 30408704;
	private File currentWorkingDirectory;

	public static void main(String[] args) throws IOException {
		// Files.walkFileTree(start, new ProcessPodcasts());
		CDRWBurner wipe = new CDRWBurner();
		wipe.eraseDisk();
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
		if (attr.isRegularFile() && file.toString().contains(".mp3")) {

			if (org.apache.commons.io.FileUtils.sizeOfDirectory(OUTDIR.toFile()) > sizeOfMedium) {
				ISOCreator ic = new ISOCreator();
				ic.createIso(OUTDIR);
				return FileVisitResult.TERMINATE;
			}

			LamePod lp = new LamePod();
			System.out.println("Current file being Processed: " + file.toString());
			File returnedProcessedFile = lp.downSample(file.toFile(), currentWorkingDirectory.toPath());

			PodcastNormalizer pn = new PodcastNormalizer();
			pn.split(returnedProcessedFile, currentWorkingDirectory.toPath());

			Mp3SplitPod sp = new Mp3SplitPod();
			sp.split(returnedProcessedFile, currentWorkingDirectory.toPath());

			// ISOCreator ic = new ISOCreator();
			// ic.createIso(OUTDIR);

		} else {
			System.out.format("Skipping: %s ", file);
		}
		// System.out.println("(" + attr.size() + "bytes)");
		return CONTINUE;
	}

	// Print each directory visited.
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

	private void setCurrentWriteToDir(File inDir) {
		this.currentWorkingDirectory = inDir;
	}
}
