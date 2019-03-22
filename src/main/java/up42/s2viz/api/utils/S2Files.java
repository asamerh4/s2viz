package up42.s2viz.api.utils;

import java.io.File;
import java.util.ArrayList;


public class S2Files {

	public static ArrayList<String> listFilesForFolder(String folder, int utmzone, String latitudeband, String gridsquare, String date) {
		
		ArrayList<String> matches = new ArrayList<String>();
		File dir = new File(folder);
		File[] files = dir.listFiles((d, name) -> name.endsWith(".tif"));

		for (int i=0; i < files.length; i++) {
			if(files[i].getName().startsWith("T"+utmzone+latitudeband+gridsquare)) {
			  String[] pattern = files[i].getName().split("_");
			  if(pattern[1].startsWith(date.replace("-", ""))) {
				matches.add(files[i].getName());
			  }
			}
		}
	    return matches;
	}

}
