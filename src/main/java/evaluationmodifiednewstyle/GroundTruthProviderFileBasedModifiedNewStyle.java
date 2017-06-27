package evaluationmodifiednewstyle;

import java.io.File;

public class GroundTruthProviderFileBasedModifiedNewStyle extends GroundTruthProviderModifiedNewStyle {
	private static final String DATA_FOLDER = "groundTruth";

	@Override
	public void loadDate() {
		final File[] listOfFiles = new File(DATA_FOLDER).listFiles();
		for (int j = 0; j < listOfFiles.length; j++) {
			final String file = listOfFiles[j].getName();
			role.add(GroundTruthParserModifiedNewStyle.parse(DATA_FOLDER + File.separator + file));
		}
	}
}
