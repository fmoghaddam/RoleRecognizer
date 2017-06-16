package evaluation;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import main.Category;
import main.RoleListProvider;
import main.RoleListProviderFileBased;
import metrics.FMeasure;
import metrics.Precision;
import metrics.Recall;

public class Evaluator {

	private static Logger LOG = Logger.getLogger(Evaluator.class);

	final Precision precision;
	final Recall recall;

	final RoleListProvider roleProvider;
	final GroundTruchProvider groundTruthProvider;

	public Evaluator() throws IOException {

		precision = new Precision();
		recall = new Recall();

		groundTruthProvider = new GroundTruthProviderFileBased();
		roleProvider = new RoleListProviderFileBased();

		// roleProvider = new RoleListProviderDummy();
		roleProvider.loadRoles();
		groundTruthProvider.loadDate();
	}

	/**
	 * If a selected role partially contains ground truth role, then it is hit.
	 * Partially means: separated by space.
	 */
	public void containEvaluationWithOriginalDictionary() {
		resetMetrics();
		for(GroundTruthFile groundTruthFile:groundTruthProvider.getData()){
			for(Entry<Category, List<Tuple<String, String>>> entry:groundTruthFile.getData().entrySet()){
				final Category category = entry.getKey();
				for(Tuple<String,String> tuple: entry.getValue()){
					final String candicateText = tuple.key;
					final Set<Category> categories = roleProvider.getData().get(candicateText);
					if (categories != null) {
						if(categories.contains(category)){
							precision.addTruePositive();
							recall.addTruePositive();
						}else{
							precision.addFalsePositive();
						}
					} else {
						final Set<String> allRoles = roleProvider.getData().keySet();
						boolean found = false;
						for (String role : allRoles) {
							final Pattern pattern = Pattern.compile("(?i)" + "\\b" + candicateText + "\\b");
							final Matcher matcher = pattern.matcher(role);
							if (matcher.find()) {
								precision.addTruePositive();
								recall.addTruePositive();
								found = true;
								break;
							}
						}
						if (!found) {
							recall.addFalseNegative();
						}
					}
				}
			}
		}		
		LOG.info("containEvaluationWithOriginalDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	public void exactMatchEvaluationWithOriginalDictionary() {
		resetMetrics();
		for(GroundTruthFile groundTruthFile:groundTruthProvider.getData()){
			for(Entry<Category, List<Tuple<String, String>>> entry:groundTruthFile.getData().entrySet()){
				final Category category = entry.getKey();
				for(Tuple<String,String> tuple: entry.getValue()){
					final String candicateText = tuple.key;
					final Set<Category> categories = roleProvider.getData().get(candicateText);
					if (categories == null) {
						recall.addFalseNegative();
					} else {
						final boolean hasIntesection = categories.contains(category);
						if (hasIntesection) {
							precision.addTruePositive();
							recall.addTruePositive();
						} else {
							precision.addFalsePositive();
						}
					}
				}
			}
		}
		LOG.info("exactMatchEvaluationWithOriginalDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	public void evaluationWithNERDictionary() {
		resetMetrics();

		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());

		for(GroundTruthFile groundTruthFile:groundTruthProvider.getData()){
			for(Entry<Category, List<Tuple<String, String>>> entry:groundTruthFile.getData().entrySet()){
				final Category category = entry.getKey();
				for(Tuple<String,String> tuple: entry.getValue()){
					final String candidateText = tuple.key;
					final String convretedToNERText = NERTagger.runTagger(candidateText);
					final String replaceWordsWithTags = NERTagger.replaceWordsWithTags(convretedToNERText,candidateText).toLowerCase();

					final Set<Category> categories = generateNERDictionary.get(replaceWordsWithTags);
					if (categories == null) {
						recall.addFalseNegative();
					} else {
						final Set<Category> newSet = new HashSet<>(categories);
						final boolean hasIntesection = newSet.contains(category);
						if (hasIntesection) {
							precision.addTruePositive();
							recall.addTruePositive();
						} else {
							precision.addFalsePositive();
						}
					}

				}
			}
		}

		LOG.info("evaluationWithNERDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	private void resetMetrics() {
		precision.reset();
		recall.reset();
	}

	public void evaluationWithPOSAndNERDictionary() {
		resetMetrics();

		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger.generatePOSAndNERDictionary(generateNERDictionary);

		for(GroundTruthFile groundTruthFile:groundTruthProvider.getData()){
			for(Entry<Category, List<Tuple<String, String>>> entry:groundTruthFile.getData().entrySet()){
				final Category category = entry.getKey();
				for(Tuple<String,String> tuple: entry.getValue()){
					final String candidateText = tuple.key;
					final String convretedToNERText = POSTagger.runPOSTaggerWithNoNER(NERTagger.runTagger(candidateText));
					final String replaceWordsWithTags = POSTagger.replaceWordsWithTagsButNotNER(convretedToNERText,candidateText).toLowerCase();

					final Set<Category> categories = generatePOSDictionary.get(replaceWordsWithTags);
					if (categories == null) {
						recall.addFalseNegative();
					} else {
						final Set<Category> newSet = new HashSet<>(categories);
						final boolean hasIntesection = newSet.contains(category);
						if (hasIntesection) {
							precision.addTruePositive();
							recall.addTruePositive();
						} else {
							precision.addFalsePositive();
						}
					}

				}
			}
		}

		LOG.info("evaluationWithPOSAndNERDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	public void evaluationWithPOSDictionary() {
		resetMetrics();

		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger.generatePOSDictionary(generateNERDictionary);

		for(GroundTruthFile groundTruthFile:groundTruthProvider.getData()){
			for(Entry<Category, List<Tuple<String, String>>> entry:groundTruthFile.getData().entrySet()){
				final Category category = entry.getKey();
				for(Tuple<String,String> tuple: entry.getValue()){
					final String candidateText = tuple.key;
					final String convretedToNERText = POSTagger.runPOSTagger(candidateText);
					final String replaceWordsWithTags = POSTagger.replaceWordsWithTags(convretedToNERText,candidateText).toLowerCase();

					final Set<Category> categories = generatePOSDictionary.get(replaceWordsWithTags);
					if (categories == null) {
						recall.addFalseNegative();
					} else {
						final Set<Category> newSet = new HashSet<>(categories);
						final boolean hasIntesection = newSet.contains(category);
						if (hasIntesection) {
							precision.addTruePositive();
							recall.addTruePositive();
						} else {
							precision.addFalsePositive();
						}
					}

				}
			}
		}

		LOG.info("evaluationWithPOSDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

}
