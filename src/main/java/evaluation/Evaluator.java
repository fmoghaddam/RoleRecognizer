package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ie.NERClassifierCombiner;
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

	final Map<String, Set<Category>> nerDictionary;
	final Map<String, Set<String>> nerDictionaryStatistic;

	/**
	 * NER Models
	 */
	final String model1 = "nermodel/english.all.3class.distsim.crf.ser.gz";
	final String model2 = "nermodel/english.conll.4class.distsim.crf.ser.gz";
	final String model3 = "nermodel/english.muc.7class.distsim.crf.ser.gz";
	final NERClassifierCombiner classifier;

	public Evaluator() throws IOException {

		classifier = new NERClassifierCombiner(model1, model2, model3);

		precision = new Precision();
		recall = new Recall();

		groundTruthProvider = new GroundTruthProviderFileBased();
		roleProvider = new RoleListProviderFileBased();

		nerDictionary = new LinkedHashMap<>();
		nerDictionaryStatistic = new LinkedHashMap<>();

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
		for (Entry<String, Set<Category>> entry : groundTruthProvider.getData().entrySet()) {
			final String groundTruthRole = entry.getKey();
			final Set<Category> categories = roleProvider.getValues().get(groundTruthRole);
			if (categories != null) {
				final Set<Category> newSet = new HashSet<>(categories);
				final Set<Category> groundTruthCategories = entry.getValue();
				final boolean hasIntesection = hasIntersection(newSet, groundTruthCategories);
				if (hasIntesection) {
					precision.addTruePositive();
					recall.addTruePositive();
				} else {
					precision.addFalsePositive();
				}
			} else {
				final Set<String> allRoles = roleProvider.getValues().keySet();
				boolean found = false;
				for (String role : allRoles) {
					final Pattern pattern = Pattern.compile("(?i)" + "\\b" + groundTruthRole + "\\b");
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
		LOG.info("containEvaluationWithOriginalDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	public void exactMatchEvaluationWithOriginalDictionary() {
		resetMetrics();
		for (Entry<String, Set<Category>> entry : groundTruthProvider.getData().entrySet()) {
			final Set<Category> categories = roleProvider.getValues().get(entry.getKey());
			if (categories == null) {
				recall.addFalseNegative();
			} else {
				final Set<Category> newSet = new HashSet<>(categories);
				final boolean hasIntesection = hasIntersection(newSet, entry.getValue());
				if (hasIntesection) {
					precision.addTruePositive();
					recall.addTruePositive();
				} else {
					precision.addFalsePositive();
				}
			}
		}
		
		LOG.info("exactMatchEvaluationWithOriginalDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	private static boolean hasIntersection(Set<Category> set1, Set<Category> set2) {
		for (Category cat : set1) {
			if (set2.contains(cat)) {
				return true;
			}
		}
		return false;
	}

	public void evaluationWithNERDictionary() {
		resetMetrics();
		final List<String> nerTaggedStrings = runNERTagger(roleProvider.getValues().keySet());
		final String[] nerTaggedStringsArray = nerTaggedStrings.toArray(new String[nerTaggedStrings.size()]);

		int i=0;
		for (String fromOriginalDict : roleProvider.getValues().keySet()) {
			final String nerTaggedResultReplaced = replaceWordsWithTags(nerTaggedStringsArray[i++], fromOriginalDict);
			addToNERDictionary(nerTaggedResultReplaced, fromOriginalDict);
		}


		for (Entry<String, Set<Category>> entry : groundTruthProvider.getData().entrySet()) {
			final String candidateText = entry.getKey();
			final List<String> convretedToNERText = runNERTagger(new HashSet<String>(Arrays.asList(candidateText)));
			final String replaceWordsWithTags = replaceWordsWithTags(convretedToNERText.get(0),candidateText).toLowerCase();
			final Set<Category> categories = nerDictionary.get(replaceWordsWithTags);
			if (categories == null) {
				recall.addFalseNegative();
			} else {
				final Set<Category> newSet = new HashSet<>(categories);
				final boolean hasIntesection = hasIntersection(newSet, entry.getValue());
				if (hasIntesection) {
					precision.addTruePositive();
					recall.addTruePositive();
				} else {
					precision.addFalsePositive();
				}
			}
		}

		LOG.info("evaluationWithNERDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");


	}

	private List<String> runNERTagger(Set<String> originalDictionary) {
		final List<String> result = new ArrayList<>();
		try {			
			for (String fromOriginalDict : originalDictionary) {
				final String nerTaggedResult = classifier.classifyWithInlineXML(fromOriginalDict);
				result.add(nerTaggedResult);
			}
		} catch (ClassCastException  e) {
			e.printStackTrace();
		}

		return result;
	}

	private void addToNERDictionary(final String tagedText,final String originalText) {
		final Set<Category> categorySet = roleProvider.getValues().get(originalText);
		final Set<String> freqSet = nerDictionaryStatistic.get(tagedText);
		if (freqSet == null) {
			final Set<String> set = new HashSet<>();
			set.add(originalText);
			nerDictionary.put(tagedText.toLowerCase(), categorySet);
			nerDictionaryStatistic.put(tagedText, set);
		} else {
			freqSet.add(originalText);			
			nerDictionaryStatistic.put(tagedText, freqSet);

			Set<Category> set = nerDictionary.get(tagedText);
			if(set==null){
				nerDictionary.put(tagedText.toLowerCase(), categorySet);
			}else{
				final Set<Category> newSet = new HashSet<>(categorySet);
				newSet.addAll(set);
				nerDictionary.put(tagedText.toLowerCase(), newSet);
			}
		}
	}

	private String replaceWordsWithTags(String nerTaggedResult, String originalText) {
		String result = new String(originalText);
		final Document doc = Jsoup.parse(nerTaggedResult);

		for (final NER_TAG tag : NER_TAG.values()) {
			final Elements possibleCandidates = doc.select(tag.text);
			for (Element element : possibleCandidates) {
				result = result.replaceAll(element.html(), element.nodeName().toUpperCase());
			}
		}

		return result;
	}

	private void resetMetrics() {
		precision.reset();
		recall.reset();
	}

	enum NER_TAG {
		PERSON("PERSON"), LOCATION("LOCATION"), ORGANIZATION("ORGANIZATION"), MISC("MISC"), ORDINAL("ORDINAL"), MONEY(
				"MONEY"), NUMBER("NUMBER"), DATE("DATE"), PERCENT("PERCENT"), TIME("TIME");

		private String text;

		NER_TAG(String text) {
			this.text = text;
		}
	}

}
