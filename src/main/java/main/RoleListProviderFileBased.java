package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import util.ColorUtil;

public class RoleListProviderFileBased extends RoleListProvider {

	private static final String DATA_FOLDER = "data";

	@Override
	public void loadRoles() {
		try {
			final File[] listOfFiles = new File(DATA_FOLDER).listFiles();
			for (int j = 0; j < listOfFiles.length; j++) {
				final String file = listOfFiles[j].getName();
				BufferedReader br = new BufferedReader(new FileReader(DATA_FOLDER + File.separator + file));
				String line;
				while ((line = br.readLine()) != null) {
					roleMap.put(line, Category.resolve(file));
				}
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sortBasedOnLenghth(Order.DESC);
		ColorUtil.fill(new HashSet<>(roleMap.values()));
	}

}
