package net.rotge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CDRWBurner {

	// took 512 seconds to completely erase CDRW using blank=all
	// took 25 seconds for blank=fast
	// cdrecord -v -force blank=disk dev=/dev/cdrom
	final String PROGRAMTORUN = "/usr/bin/wodim";
	final String VERBOSEOUTPUT = "-v";
	final String FORCEERASE = "-force";
	final String DUMMYWRITE = "-dummy";
	final String LOCATIONOFCDRW = "/dev/sr0";
	final String BLANKTYPE = "-blank=fast";

	public void eraseDisk() throws IOException {
	String[] eraseCDRWCommand = { PROGRAMTORUN, VERBOSEOUTPUT,DUMMYWRITE,BLANKTYPE,LOCATIONOFCDRW };
	ProcessBuilder probuilder = new ProcessBuilder(eraseCDRWCommand);
	Process process = probuilder.start();

	InputStream is = process.getErrorStream();
	InputStreamReader isr = new InputStreamReader(is);
	BufferedReader br = new BufferedReader(isr);
	String line;
	while ((line = br.readLine()) != null) {
			System.out.println(line);
	}

	try {
		int exitValue = process.waitFor();
		System.out.println("ExitValue is " + exitValue);
	} catch (InterruptedException e1) {
		e1.printStackTrace();
	}

}
}
