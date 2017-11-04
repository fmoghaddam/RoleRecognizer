package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import model.Category;
import model.DataSourceType;
import model.Order;
import util.ColorUtil;

public class RoleListProviderFileBased extends RoleListProvider {

	private static final String DATA_FOLDER = "data";

	public RoleListProviderFileBased() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see main.RoleListProvider#loadRoles()
	 */
	@Override
	public void loadRoles(DataSourceType dataSourceType) {
		roleMap.clear();
		String dataSubFolder;
		switch (dataSourceType) {
		case WIKIPEDIA:
			dataSubFolder = DATA_FOLDER+File.separator + "wikipedia";
			break;
		case WIKIDATA:
			dataSubFolder = DATA_FOLDER+File.separator + "wikidata";
			break;
		case ALL:
			dataSubFolder = DATA_FOLDER+File.separator + "all";
			break;
		default:
			dataSubFolder = DATA_FOLDER+File.separator + "wikipedia";
			break;
		}
		try {
			final File[] listOfFiles = new File(dataSubFolder).listFiles();
			for (int j = 0; j < listOfFiles.length; j++) {
				final String file = listOfFiles[j].getName();
				BufferedReader br = new BufferedReader(new FileReader(dataSubFolder + File.separator + file));
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
		ColorUtil.fillExtra(Arrays.asList(Category.ROLE,Category.NO_ROLE));
	}
}
