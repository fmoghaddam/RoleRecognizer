package evaluation;

import java.io.File;

@Deprecated
public class GroundTruthProviderFileBased extends GroundTruchProvider {
	private static final String DATA_FOLDER = "groundTruth";

	@Override
	public void loadDate() {
		final File[] listOfFiles = new File(DATA_FOLDER).listFiles();
		for (int j = 0; j < listOfFiles.length; j++) {
			final String file = listOfFiles[j].getName();
			data.add(GroundTruthParser.parse(DATA_FOLDER + File.separator + file));
		}
	}
}
