package net.rotge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) throws IOException {

		Path defaultOutputDir = Paths.get("/tmp/output");
		// public final static String rootDir = "/media/caustic/toshiba/podcasts/2015";
		// public final static Path start = Paths.get("/media/caustic/toshiba/podcasts/2015");
		Path defaultInputStartDirectory = Paths.get("/tmp/root");
		// public final static Path start = Paths.get("/media/caustic/toshiba/podcasts/2015/Secular Media Network");
		// final static long sizeOfMedium = 734003200; // this is 700mb size of CDRW in bytes
		long defaultSizeOfMedium = 30408704;

		Files.walkFileTree(defaultInputStartDirectory,
				new ProcessPodcasts(defaultOutputDir, defaultInputStartDirectory, defaultSizeOfMedium));
		// CDRWBurner wipe = new CDRWBurner();
		// wipe.eraseDisk();
	}

}
