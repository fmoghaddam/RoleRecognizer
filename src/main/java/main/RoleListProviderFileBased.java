package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import util.ColorUtil;

public class RoleListProviderFileBased extends RoleListProvider {

	private static final String DATA_FOLDER = "data";

	/*
	 * (non-Javadoc)
	 * 
	 * @see main.RoleListProvider#loadRoles()
	 */
	@Override
	public void loadRoles() {
		try {
			final File[] listOfFiles = new File(DATA_FOLDER).listFiles();
			for (int j = 0; j < listOfFiles.length; j++) {
				final String file = listOfFiles[j].getName();
				BufferedReader br = new BufferedReader(new FileReader(DATA_FOLDER + File.separator + file));
				String line;
				while ((line = br.readLine()) != null) {
					final Set<Category> categorySet = roleMap.get(line);
					if (categorySet == null || categorySet.isEmpty()) {
						final Set<Category> catSet = new HashSet<>();
						catSet.add(Category.resolve(file));
						roleMap.put(line, catSet);
					} else {
						categorySet.add(Category.resolve(file));
						roleMap.put(line, categorySet);
					}
				}
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sortBasedOnLenghth(Order.DESC);
		ColorUtil.fill(roleMap.values().stream().flatMap(p -> p.stream()).distinct().collect(Collectors.toList()));
	}
}
