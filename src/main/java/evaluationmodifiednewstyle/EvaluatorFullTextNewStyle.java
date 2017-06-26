package evaluationmodifiednewstyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import evaluation.NERTagger;
import evaluation.POSTagger;
import main.RoleListProvider;
import main.RoleListProviderFileBased;
import metrics.FMeasure;
import metrics.Precision;
import metrics.Recall;
import model.Category;
import model.NER_TAG;
import model.Position;
import model.Role;
import model.TagPosition;
import model.TagPositions;

public class EvaluatorFullTextNewStyle {

	private static Logger LOG = Logger.getLogger(EvaluatorFullTextNewStyle.class);

	final Precision precision;
	final Recall recall;

	final RoleListProvider originalRoleProvider;
	final GroundTruchProviderModifiedNewStyle groundTruthProvider;

	public EvaluatorFullTextNewStyle() throws IOException {

		precision = new Precision();
		recall = new Recall();

		groundTruthProvider = new GroundTruthProviderFileBasedModifiedNewStyle();
		originalRoleProvider = new RoleListProviderFileBased();

		originalRoleProvider.loadRoles();
		groundTruthProvider.loadDate();

		printGroundTruthStatistics(groundTruthProvider.getRoles());
	}

	static Map<Category, Integer> printGroundTruthStatistics(Set<GroundTruthFileModifiedNewStyle> groundTruthes) {
		final Map<Category, Integer> statistic = new HashMap<>();
		for (final GroundTruthFileModifiedNewStyle groundTruth : groundTruthes) {
			groundTruth.getRoles().forEach(p -> {
				final Category resolveCategory = Category.resolve(p.getXmlAttributes().get("type").toLowerCase());
				final Integer value = statistic.get(resolveCategory);
				if (value == null) {
					statistic.put(resolveCategory, 1);
				} else {
					statistic.put(resolveCategory, value + 1);
				}
			});
		}
		LOG.info("-------------------------------------");
		for (Entry<Category, Integer> entry : statistic.entrySet()) {
			LOG.info(entry.getKey() + "==" + entry.getValue());
		}
		LOG.info("-------------------------------------");
		return statistic;
	}

	/**
	 * Working
	 */
	public void exactMatchEvaluationWithOriginalDictionary() {
		resetMetrics();
		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : originalRoleProvider.getData().entrySet()) {

				final String dictionaryRole = roleEntity.getKey();

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(originalFullText);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						if (hasPositionOverlapByToken(GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(
								originalFullText, candicatePosition), role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								precision.addTruePositive();
								recall.addTruePositive();
								groundTruthFileCopyTemp.remove(role);
								found = true;
								break;
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("exactMatchEvaluationWithOriginalDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	private boolean hasPositionOverlapByToken(Position candicatePosition, Position rolePhasePosition) {
		if (candicatePosition.getStartIndex() <= rolePhasePosition.getStartIndex()
				&& candicatePosition.getEndIndex() >= rolePhasePosition.getEndIndex()) {
			return true;
		} else if (rolePhasePosition.getStartIndex() >= candicatePosition.getStartIndex()
				&& rolePhasePosition.getStartIndex() <= candicatePosition.getEndIndex()) {
			return true;
		} else if (rolePhasePosition.getEndIndex() >= candicatePosition.getStartIndex()
				&& rolePhasePosition.getEndIndex() <= candicatePosition.getEndIndex()) {
			return true;
		}
		return false;
	}

	boolean hasPositionOverlap(Position candicatePosition, Position rolePhasePosition) {
		if (candicatePosition.getStartIndex() <= rolePhasePosition.getStartIndex()
				&& candicatePosition.getEndIndex() >= rolePhasePosition.getEndIndex()) {
			return true;
		} else if (rolePhasePosition.getStartIndex() >= candicatePosition.getStartIndex()
				&& rolePhasePosition.getStartIndex() <= candicatePosition.getEndIndex()) {
			return true;
		} else if (rolePhasePosition.getEndIndex() >= candicatePosition.getStartIndex()
				&& rolePhasePosition.getEndIndex() <= candicatePosition.getEndIndex()) {
			return true;
		}
		return false;
	}

	/**
	 * Working
	 */
	public void exactMatchEvaluationWithOriginalDictionaryConsiderCategory() {
		resetMetrics();
		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : originalRoleProvider.getData().entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(originalFullText);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						if (hasPositionOverlapByToken(GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(
								originalFullText, candicatePosition), role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								final Category category = Category.resolve(role.getXmlAttributes().get("type"));
								final Set<Category> intesection = hasIntersection(
										new HashSet<>(Arrays.asList(category)), dictionaryCategories);
								if (intesection != null && !intesection.isEmpty()) {
									precision.addTruePositive();
									recall.addTruePositive();
									groundTruthFileCopyTemp.remove(role);
									found = true;
									break;
								} else {
									precision.addFalsePositive();
									found = true;
									break;
								}
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("exactMatchEvaluationWithOriginalDictionaryConsdeirCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	private Set<Category> hasIntersection(Set<Category> collect, Set<Category> dictionaryCategories) {
		Set<Category> intersection = new HashSet<>();
		for (Category cat : collect) {
			if (dictionaryCategories.contains(cat)) {
				intersection.add(cat);
			}
		}
		return intersection;
	}

	/**
	 * Working
	 */
	public void evaluationWithNERDictionary() {
		resetMetrics();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger
				.generateDictionary(originalRoleProvider.getData());

		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final String taggedFullText = NERTagger.replaceWordsWithTags(NERTagger.runTagger(originalFullText),
					originalFullText);
			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : generateNERDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();

				/**
				 * Ignoring all the tags which is only 1 word and we know that
				 * they are not a role
				 */
				if (NER_TAG.resolve(dictionaryRole.substring(1, dictionaryRole.length() - 1)) != null) {
					continue;
				}
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullText);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						final Position tokenNumberStanfordTokenizerPosition = GroundTruthParserModifiedNewStyle
								.getTokenNumberStanfordTokenizer(taggedFullText, candicatePosition);
						if (hasPositionOverlapByToken(tokenNumberStanfordTokenizerPosition,
								role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								precision.addTruePositive();
								recall.addTruePositive();
								groundTruthFileCopyTemp.remove(role);
								found = true;
								break;
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	/**
	 * Working
	 */
	public void evaluationWithNERDictionaryConsiderCategory() {
		resetMetrics();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger
				.generateDictionary(originalRoleProvider.getData());

		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final String taggedFullText = NERTagger.replaceWordsWithTags(NERTagger.runTagger(originalFullText),
					originalFullText);
			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : generateNERDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				/**
				 * Ignoring all the tags which is only 1 word and we know that
				 * they are not a role
				 */
				if (NER_TAG.resolve(dictionaryRole.substring(1, dictionaryRole.length() - 1)) != null) {
					continue;
				}
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullText);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						final Position tokenNumberStanfordTokenizerPosition = GroundTruthParserModifiedNewStyle
								.getTokenNumberStanfordTokenizer(taggedFullText, candicatePosition);
						if (hasPositionOverlapByToken(tokenNumberStanfordTokenizerPosition,
								role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								final Category category = Category.resolve(role.getXmlAttributes().get("type"));
								final Set<Category> intesection = hasIntersection(
										new HashSet<>(Arrays.asList(category)), dictionaryCategories);
								if (intesection != null && !intesection.isEmpty()) {
									precision.addTruePositive();
									recall.addTruePositive();
									groundTruthFileCopyTemp.remove(role);
									found = true;
									break;
								} else {
									precision.addFalsePositive();
									found = true;
									break;
								}
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERDictionaryConsiderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	//
	// private Map<String, List<Tuple<Category, String>>> runNEROnGroundTruth(
	// Map<String, List<Tuple<Category, String>>> groundTruthData) {
	//
	// final Map<String, List<Tuple<Category, String>>> result = new
	// TreeMap<>();
	//
	// for (Entry<String, List<Tuple<Category, String>>> entry :
	// groundTruthData.entrySet()) {
	// final String replaceWordsWithTags =
	// NERTagger.replaceWordsWithTags(NERTagger.runTagger(entry.getKey()),
	// entry.getKey());
	//
	// final List<Tuple<Category, String>> list =
	// result.get(replaceWordsWithTags);
	// if (list == null) {
	// result.put(replaceWordsWithTags, entry.getValue());
	// } else {
	// List<Tuple<Category, String>> newList = new ArrayList<>(list);
	// newList.addAll(entry.getValue());
	// result.put(replaceWordsWithTags, newList);
	// }
	// }
	//
	// return result;
	// }
	//

	private void resetMetrics() {
		precision.reset();
		recall.reset();
	}

	/**
	 * Working - check again- currently produce only zero
	 */
	public void evaluationWithPOSAndNERDictionary() {
		resetMetrics();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger
				.generateDictionary(originalRoleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(generateNERDictionary);

		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final String taggedFullTextNer = NERTagger.replaceWordsWithTags(NERTagger.runTagger(originalFullText),
					originalFullText);
			final String taggedFullTextNERPOS = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTagger(taggedFullTextNer), originalFullText);

			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : generatePOSDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();

				/**
				 * Ignoring all the tags which is only 1 word and we know that
				 * they are not a role
				 */
				if (NER_TAG.resolve(dictionaryRole.substring(1, dictionaryRole.length() - 1)) != null) {
					continue;
				}

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullTextNERPOS);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						final Position tokenNumberStanfordTokenizerPosition = GroundTruthParserModifiedNewStyle
								.getTokenNumberStanfordTokenizer(taggedFullTextNERPOS, candicatePosition);
						if (hasPositionOverlapByToken(tokenNumberStanfordTokenizerPosition,
								role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								precision.addTruePositive();
								recall.addTruePositive();
								groundTruthFileCopyTemp.remove(role);
								found = true;
								break;
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithPOSAndNERDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	//
	// private Map<String, List<Tuple<Category, String>>>
	// runNERPOSOnGroundTruth(?
	// Map<String, List<Tuple<Category, String>>> groundTruthData) {
	//
	// final Map<String, List<Tuple<Category, String>>> result = new
	// TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	//
	// for (Entry<String, List<Tuple<Category, String>>> entry :
	// groundTruthData.entrySet()) {
	// final String replaceWordsWithNERTags =
	// NERTagger.replaceWordsWithTags(NERTagger.runTagger(entry.getKey()),
	// entry.getKey());
	// final String replaceWordsWithNERPOSTags =
	// POSTagger.replaceWordsWithTagsButNotNER(
	// POSTagger.runPOSTaggerWithNoNER(replaceWordsWithNERTags),
	// replaceWordsWithNERTags);
	//
	// final List<Tuple<Category, String>> list =
	// result.get(replaceWordsWithNERPOSTags);
	// if (list == null) {
	// result.put(replaceWordsWithNERPOSTags, entry.getValue());
	// } else {
	// List<Tuple<Category, String>> newList = new ArrayList<>(list);
	// newList.addAll(entry.getValue());
	// result.put(replaceWordsWithNERPOSTags, newList);
	// }
	// }
	// return result;
	// }
	//
	/**
	 * Working - check again- currently produce only zero
	 */
	public void evaluationWithPOSAndNERDictionaryConsiderCategory() {
		resetMetrics();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger
				.generateDictionary(originalRoleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(generateNERDictionary);

		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final String taggedFullTextNer = NERTagger.replaceWordsWithTags(NERTagger.runTagger(originalFullText),
					originalFullText);
			final String taggedFullTextNERPOS = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTagger(taggedFullTextNer), originalFullText);

			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : generatePOSDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				/**
				 * Ignoring all the tags which is only 1 word and we know that
				 * they are not a role
				 */
				if (NER_TAG.resolve(dictionaryRole.substring(1, dictionaryRole.length() - 1)) != null) {
					continue;
				}

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullTextNERPOS);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						final Position tokenNumberStanfordTokenizerPosition = GroundTruthParserModifiedNewStyle
								.getTokenNumberStanfordTokenizer(taggedFullTextNERPOS, candicatePosition);
						if (hasPositionOverlapByToken(tokenNumberStanfordTokenizerPosition,
								role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								final Category category = Category.resolve(role.getXmlAttributes().get("type"));
								final Set<Category> intesection = hasIntersection(
										new HashSet<>(Arrays.asList(category)), dictionaryCategories);
								if (intesection != null && !intesection.isEmpty()) {
									precision.addTruePositive();
									recall.addTruePositive();
									groundTruthFileCopyTemp.remove(role);
									found = true;
									break;
								} else {
									precision.addFalsePositive();
									found = true;
									break;
								}
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithPOSAndNERDictionaryConsiderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	/**
	 * Working - check again- currently produce only zero
	 */
	public void evaluationWithNERAndThenPOSDictionary() {
		resetMetrics();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger
				.generateDictionary(originalRoleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(generateNERDictionary);

		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final String taggedFullTextNer = NERTagger.replaceWordsWithTags(NERTagger.runTagger(originalFullText),
					originalFullText);
			final String taggedFullTextNERPOS = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTagger(taggedFullTextNer), originalFullText);

			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : generatePOSDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();

				/**
				 * Ignoring all the tags which is only 1 word and we know that
				 * they are not a role
				 */
				if (NER_TAG.resolve(dictionaryRole.substring(1, dictionaryRole.length() - 1)) != null) {
					continue;
				}

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullTextNer);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final String taggedFoundRole = POSTagger
							.replaceWordsWithTags(POSTagger.runPOSTagger(foundRoleInText), foundRoleInText);
					if (!generatePOSDictionary.containsKey(taggedFoundRole)) {
						continue;
					}
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						final Position tokenNumberStanfordTokenizerPosition = GroundTruthParserModifiedNewStyle
								.getTokenNumberStanfordTokenizer(taggedFullTextNERPOS, candicatePosition);
						if (hasPositionOverlapByToken(tokenNumberStanfordTokenizerPosition,
								role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								precision.addTruePositive();
								recall.addTruePositive();
								groundTruthFileCopyTemp.remove(role);
								found = true;
								break;
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERAndThenPOSDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	// @SuppressWarnings("unused")
	// private Map<String, List<Tuple<Category, String>>> runPOSOnGroundTruth(
	// Map<String, List<Tuple<Category, String>>> groundTruthData) {
	// final Map<String, List<Tuple<Category, String>>> result = new
	// TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	//
	// for (Entry<String, List<Tuple<Category, String>>> entry :
	// groundTruthData.entrySet()) {
	// final String replaceWordsWithPOSTags = POSTagger
	// .replaceWordsWithTagsButNotNER(POSTagger.runPOSTaggerWithNoNER(entry.getKey()),
	// entry.getKey());
	//
	// final List<Tuple<Category, String>> list =
	// result.get(replaceWordsWithPOSTags);
	// if (list == null) {
	// result.put(replaceWordsWithPOSTags, entry.getValue());
	// } else {
	// List<Tuple<Category, String>> newList = new ArrayList<>(list);
	// newList.addAll(entry.getValue());
	// result.put(replaceWordsWithPOSTags, newList);
	// }
	// }
	// return result;
	// }
	//
	/**
	 * Working - check again- currently produce only zero
	 */
	public void evaluationWithNERAndThenPOSDictionaryConsiderCategory() {
		resetMetrics();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger
				.generateDictionary(originalRoleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(generateNERDictionary);

		for (final GroundTruthFileModifiedNewStyle groundTruthFile : groundTruthProvider.getRoles()) {
			final TagPositions tagPositions = new TagPositions();
			final String originalFullText = groundTruthFile.getFullContent();
			final String taggedFullTextNer = NERTagger.replaceWordsWithTags(NERTagger.runTagger(originalFullText),
					originalFullText);
			final String taggedFullTextNERPOS = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTagger(taggedFullTextNer), originalFullText);

			final List<Role> groundTruthFileCopy = groundTruthFile.getRoles();
			final List<Role> groundTruthFileCopyTemp = groundTruthFile.getRoles();
			for (final Entry<String, Set<Category>> roleEntity : generatePOSDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				/**
				 * Ignoring all the tags which is only 1 word and we know that
				 * they are not a role
				 */
				if (NER_TAG.resolve(dictionaryRole.substring(1, dictionaryRole.length() - 1)) != null) {
					continue;
				}

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullTextNer);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final String taggedFoundRole = POSTagger
							.replaceWordsWithTags(POSTagger.runPOSTagger(foundRoleInText), foundRoleInText);
					if (!generatePOSDictionary.containsKey(taggedFoundRole)) {
						continue;
					}
					final TagPosition candicatePosition = new TagPosition(foundRoleInText, matcher.start(),
							matcher.end());
					if (tagPositions.alreadyExist(candicatePosition)) {
						continue;
					}
					tagPositions.add(candicatePosition);
					groundTruthFileCopy.clear();
					groundTruthFileCopy.addAll(groundTruthFileCopyTemp);
					boolean found = false;
					for (final Role role : groundTruthFileCopy) {
						final Position tokenNumberStanfordTokenizerPosition = GroundTruthParserModifiedNewStyle
								.getTokenNumberStanfordTokenizer(taggedFullTextNERPOS, candicatePosition);
						if (hasPositionOverlapByToken(tokenNumberStanfordTokenizerPosition,
								role.getRolePhaseTokenPosition())) {
							if (foundRoleInText.toLowerCase().contains(role.getHeadRole().toLowerCase())) {
								final Category category = Category.resolve(role.getXmlAttributes().get("type"));
								final Set<Category> intesection = hasIntersection(
										new HashSet<>(Arrays.asList(category)), dictionaryCategories);
								if (intesection != null && !intesection.isEmpty()) {
									precision.addTruePositive();
									recall.addTruePositive();
									groundTruthFileCopyTemp.remove(role);
									found = true;
									break;
								} else {
									precision.addFalsePositive();
									found = true;
									break;
								}
							} else {
								precision.addFalsePositive();
								found = true;
								break;
							}
						}
					}
					if (!found) {
						precision.addFalsePositive();
					}
				}
			}
			for (int i = 0; i < groundTruthFileCopyTemp.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERAndThenPOSDictionaryConsiderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}
}
