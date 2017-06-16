package evaluationmodified;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import evaluation.NERTagger;
import evaluation.POSTagger;
import evaluation.Tuple;
import main.Category;
import main.RoleListProvider;
import main.RoleListProviderFileBased;
import metrics.FMeasure;
import metrics.Precision;
import metrics.Recall;

public class EvaluatorFullText {

	private static Logger LOG = Logger.getLogger(EvaluatorFullText.class);

	final Precision precision;
	final Recall recall;

	final RoleListProvider roleProvider;
	final GroundTruchProviderModified groundTruthProvider;

	public EvaluatorFullText() throws IOException {

		precision = new Precision();
		recall = new Recall();

		groundTruthProvider = new GroundTruthProviderFileBasedModified();
		roleProvider = new RoleListProviderFileBased();

		// roleProvider = new RoleListProviderDummy();
		roleProvider.loadRoles();
		groundTruthProvider.loadDate();

		printGroundTruthStatistics(groundTruthProvider.getData());
	}

	/**
	 * If a selected role partially contains ground truth role, then it is hit.
	 * Partially means: separated by space.
	 * @param groundTruth 
	 */
	// public void containEvaluationWithOriginalDictionary() {
	// resetMetrics();
	// for(GroundTruthFile groundTruthFile:groundTruthProvider.getData()){
	// for(Entry<Category, List<Tuple<String, String>>>
	// entry:groundTruthFile.getData().entrySet()){
	// final Category category = entry.getKey();
	// for(Tuple<String,String> tuple: entry.getValue()){
	// final String candicateText = tuple.key;
	// final Set<Category> categories =
	// roleProvider.getValues().get(candicateText);
	// if (categories != null) {
	// if(categories.contains(category)){
	// precision.addTruePositive();
	// recall.addTruePositive();
	// }else{
	// precision.addFalsePositive();
	// }
	// } else {
	// final Set<String> allRoles = roleProvider.getValues().keySet();
	// boolean found = false;
	// for (String role : allRoles) {
	// final Pattern pattern = Pattern.compile("(?i)" + "\\b" + candicateText +
	// "\\b");
	// final Matcher matcher = pattern.matcher(role);
	// if (matcher.find()) {
	// precision.addTruePositive();
	// recall.addTruePositive();
	// found = true;
	// break;
	// }
	// }
	// if (!found) {
	// recall.addFalseNegative();
	// }
	// }
	// }
	// }
	// }
	// LOG.info("containEvaluationWithOriginalDictionary");
	// LOG.info("Precision= " + precision.getValue());
	// LOG.info("Recall= " + recall.getValue());
	// LOG.info("FMeasure= " + new FMeasure(precision.getValue(),
	// recall.getValue()).getValue());
	// LOG.info("--------------------------------------------");
	// }

	private void printGroundTruthStatistics(Set<GroundTruthFileModified> groundTruthes) {
		final Map<Category,Integer> statistic = new HashMap<>();
		for(final GroundTruthFileModified groundTruth: groundTruthes){
			groundTruth.getData().values().forEach(p-> p.forEach(x -> {
				final Integer value = statistic.get(x.key);
				if(value==null){
					statistic.put(x.key,1);
				}else{
					statistic.put(x.key,value+1);
				}
			}));
		}
		LOG.info("-------------------------------------");
		for(Entry<Category, Integer> entry:statistic.entrySet()){
			LOG.info(entry.getKey()+"=="+entry.getValue());
		}
		LOG.info("-------------------------------------");
	}

	/**
	 * Working
	 */
	public void exactMatchEvaluationWithOriginalDictionary() {
		resetMetrics();
		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();
			final Map<String, List<Tuple<Category, String>>> groundTruthFileCopy = new HashMap<>(groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : roleProvider.getData().entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				final Pattern pattern = Pattern.compile("(?im)" + "\\b" + dictionaryRole + "\\b");
				final Matcher matcher = pattern.matcher(fullText);

				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final List<Tuple<Category, String>> listOfCategory = groundTruthFileCopy.get(foundRoleInText);
					if (listOfCategory == null || listOfCategory.isEmpty()) {
						precision.addFalsePositive();
					} else {
						alreadyFound.add(foundRoleInText);
						precision.addTruePositive();
						recall.addTruePositive();

						List<Tuple<Category, String>> list = groundTruthFileCopy.get(foundRoleInText);
						if(list.size()==1){
							groundTruthFileCopy.remove(foundRoleInText);
						}else{
							final Set<Category> collect = new HashSet<>(
									list.stream().map(p -> p.key).collect(Collectors.toList()));
							final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
							if (!intesection.isEmpty()) {
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthFileCopy.get(foundRoleInText).remove(i);
										break;
									}
								}
							}
						}
					}
				}
			}
			//for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
			for (int i = 0; i < groundTruthFileCopy.keySet().size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("exactMatchEvaluationWithOriginalDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	/**
	 * Working
	 */
	public void exactMatchEvaluationWithOriginalDictionaryConsdeirCategory() {
		resetMetrics();
		//final Map<Category,Integer> truePositiveStatisticForEachCategory = new HashMap<>();
		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();
			final Map<String, List<Tuple<Category, String>>> groundTruthFileCopy = new HashMap<>(groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : roleProvider.getData().entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				final Pattern pattern = Pattern.compile("(?im)" + "\\b" + dictionaryRole + "\\b");
				final Matcher matcher = pattern.matcher(fullText);

				//final Map<String, List<Tuple<Category, String>>> groundTruthData = new TreeMap<>(
				//		String.CASE_INSENSITIVE_ORDER);
				//groundTruthData.putAll(groundTruthFile.getData());
				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					final List<Tuple<Category, String>> listOfCategoryFromGroundTruth = groundTruthFileCopy.get(foundRoleInText);

					if (listOfCategoryFromGroundTruth == null) {
						precision.addFalsePositive();
					} else {
						final Set<Category> collect = new HashSet<>(
								listOfCategoryFromGroundTruth.stream().map(p -> p.key).collect(Collectors.toList()));
						final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
						if (!intesection.isEmpty()) {
							alreadyFound.add(foundRoleInText);
							precision.addTruePositive();
							recall.addTruePositive();

							List<Tuple<Category, String>> list = groundTruthFileCopy.get(foundRoleInText);
							if(list.size()==1){
								groundTruthFileCopy.remove(foundRoleInText);
							}else{
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthFileCopy.get(foundRoleInText).remove(i);
										break;
									}
								}
							}

							//							for(Category cat:intesection){
							//								final Integer integer = truePositiveStatisticForEachCategory.get(cat);
							//								if(integer==null){
							//									truePositiveStatisticForEachCategory.put(cat, 1);
							//								}else{
							//									truePositiveStatisticForEachCategory.put(cat, integer+1);
							//								}
							//							}
							//							
							//							
							//							List<Tuple<Category, String>> list = groundTruthFile.getData().get(foundRoleInText);
							//							if(list.size()==1){
							//								 groundTruthFile.getData().remove(foundRoleInText);
							//							}else{
							//								for(int i=0;i<list.size();i++){
							//									if(intesection.contains(list.get(i).key)){
							//										groundTruthFile.getData().get(foundRoleInText).remove(i);
							//										break;
							//									}
							//								}
							//							}

						} else {
							precision.addFalsePositive();
						}
					}
				}
			}
			//for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
			for (int i = 0; i < groundTruthFileCopy.keySet().size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("exactMatchEvaluationWithOriginalDictionaryConsderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		//		for(Entry<Category, Integer> entry:truePositiveStatisticForEachCategory.entrySet()){
		//			LOG.info(entry.getKey()+"-" +entry.getValue());
		//		}
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
		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();
			final String taggedFullText = NERTagger.replaceWordsWithTags(NERTagger.runTagger(fullText), fullText);
			final Map<String, List<Tuple<Category, String>>> groundTruthDataTagged = runNEROnGroundTruth(
					groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : generateNERDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);

				final Matcher matcher = pattern.matcher(taggedFullText);

				//final Map<String, List<Tuple<Category, String>>> groundTruthData = new TreeMap<>(
				//		String.CASE_INSENSITIVE_ORDER);
				//groundTruthData.putAll(groundTruthFile.getData());
				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);

					final List<Tuple<Category, String>> listOfCategory = groundTruthDataTagged.get(foundRoleInText);
					if (listOfCategory == null) {
						precision.addFalsePositive();
					} else {
						alreadyFound.add(foundRoleInText);
						precision.addTruePositive();
						recall.addTruePositive();
						List<Tuple<Category, String>> list = groundTruthDataTagged.get(foundRoleInText);
						if(list.size()==1){
							groundTruthDataTagged.remove(foundRoleInText);
						}else{

							final Set<Category> collect = new HashSet<>(
									list.stream().map(p -> p.key).collect(Collectors.toList()));
							final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
							if (!intesection.isEmpty()) {
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthDataTagged.get(foundRoleInText).remove(i);
										break;
									}
								}
							}


						}
					}
				}
			}
			//for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
			for (int i = 0; i < groundTruthDataTagged.keySet().size(); i++) {
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
		//final Map<Category,Integer> truePositiveStatisticForEachCategory = new HashMap<>();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();
			final String taggedFullText = NERTagger.replaceWordsWithTags(NERTagger.runTagger(fullText), fullText);
			final Map<String, List<Tuple<Category, String>>> groundTruthDataTagged = runNEROnGroundTruth(
					groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : generateNERDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole );

				final Matcher matcher = pattern.matcher(taggedFullText);

				//final Map<String, List<Tuple<Category, String>>> groundTruthData = new TreeMap<>(
				//		String.CASE_INSENSITIVE_ORDER);
				//groundTruthData.putAll(groundTruthFile.getData());
				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);

					final List<Tuple<Category, String>> listOfCategory = groundTruthDataTagged.get(foundRoleInText);
					if (listOfCategory == null) {
						precision.addFalsePositive();
					} else {
						final Set<Category> collect = new HashSet<>(
								listOfCategory.stream().map(p -> p.key).collect(Collectors.toList()));
						final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
						if (!intesection.isEmpty()) {
							alreadyFound.add(foundRoleInText);
							precision.addTruePositive();
							recall.addTruePositive();

							//							for(Category cat:intesection){
							//								final Integer integer = truePositiveStatisticForEachCategory.get(cat);
							//								if(integer==null){
							//									truePositiveStatisticForEachCategory.put(cat, 1);
							//								}else{
							//									truePositiveStatisticForEachCategory.put(cat, integer+1);
							//								}
							//							}
							//							
							//							
							//							List<Tuple<Category, String>> list = groundTruthDataTagged.get(foundRoleInText);
							//							if(list.size()==1){
							//								 groundTruthFile.getData().remove(foundRoleInText);
							//							}else{
							//								for(int i=0;i<list.size();i++){
							//									if(intesection.contains(list.get(i).key)){
							//										groundTruthFile.getData().get(foundRoleInText).remove(i);
							//										break;
							//									}
							//								}
							//							}

							List<Tuple<Category, String>> list = groundTruthDataTagged.get(foundRoleInText);
							if(list.size()==1){
								groundTruthDataTagged.remove(foundRoleInText);
							}else{
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthDataTagged.get(foundRoleInText).remove(i);
										break;
									}
								}
							}

						} else {
							precision.addFalsePositive();
						}
					}
				}
			}
			for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERDictionaryConsiderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		//		for(Entry<Category, Integer> entry:truePositiveStatisticForEachCategory.entrySet()){
		//			LOG.info(entry.getKey()+"-" +entry.getValue());
		//		}
		LOG.info("--------------------------------------------");
	}

	private Map<String, List<Tuple<Category, String>>> runNEROnGroundTruth(
			Map<String, List<Tuple<Category, String>>> groundTruthData) {

		final Map<String, List<Tuple<Category, String>>> result = new TreeMap<>();

		for (Entry<String, List<Tuple<Category, String>>> entry : groundTruthData.entrySet()) {
			final String replaceWordsWithTags = NERTagger.replaceWordsWithTags(NERTagger.runTagger(entry.getKey()),
					entry.getKey());

			final List<Tuple<Category, String>> list = result.get(replaceWordsWithTags);
			if (list == null) {
				result.put(replaceWordsWithTags, entry.getValue());
			} else {
				List<Tuple<Category, String>> newList = new ArrayList<>(list);
				newList.addAll(entry.getValue());
				result.put(replaceWordsWithTags, newList);
			}
		}

		return result;
	}

	private void resetMetrics() {
		precision.reset();
		recall.reset();
	}

	/**
	 * Working
	 */
	public void evaluationWithPOSAndNERDictionary() {
		resetMetrics();

		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(generateNERDictionary);

		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();

			final String taggedFullTextNER = NERTagger.replaceWordsWithTags(NERTagger.runTagger(fullText), fullText);
			final String taggedFullTextNERPOS = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTagger(taggedFullTextNER), fullText);
			final Map<String, List<Tuple<Category, String>>> groundTruthDataTagged = runNERPOSOnGroundTruth(
					groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : generatePOSDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);
				final Matcher matcher = pattern.matcher(taggedFullTextNERPOS);

				while (matcher.find()) {

					final String foundRoleInText = matcher.group(0);

					final List<Tuple<Category, String>> listOfCategory = groundTruthDataTagged.get(foundRoleInText);
					if (listOfCategory == null) {
						precision.addFalsePositive();
					} else {
						alreadyFound.add(foundRoleInText);
						precision.addTruePositive();
						recall.addTruePositive();
						
						List<Tuple<Category, String>> list = groundTruthDataTagged.get(foundRoleInText);
						if(list.size()==1){
							groundTruthDataTagged.remove(foundRoleInText);
						}else{
							final Set<Category> collect = new HashSet<>(
									list.stream().map(p -> p.key).collect(Collectors.toList()));
							final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
							if (!intesection.isEmpty()) {
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthDataTagged.get(foundRoleInText).remove(i);
										break;
									}
								}
							}
						}
					}
				}
			}
			for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERPOSDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	private Map<String, List<Tuple<Category, String>>> runNERPOSOnGroundTruth(
			Map<String, List<Tuple<Category, String>>> groundTruthData) {

		final Map<String, List<Tuple<Category, String>>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		for (Entry<String, List<Tuple<Category, String>>> entry : groundTruthData.entrySet()) {
			final String replaceWordsWithNERTags = NERTagger.replaceWordsWithTags(NERTagger.runTagger(entry.getKey()),
					entry.getKey());
			final String replaceWordsWithNERPOSTags = POSTagger.replaceWordsWithTagsButNotNER(
					POSTagger.runPOSTaggerWithNoNER(replaceWordsWithNERTags), replaceWordsWithNERTags);

			final List<Tuple<Category, String>> list = result.get(replaceWordsWithNERPOSTags);
			if (list == null) {
				result.put(replaceWordsWithNERPOSTags, entry.getValue());
			} else {
				List<Tuple<Category, String>> newList = new ArrayList<>(list);
				newList.addAll(entry.getValue());
				result.put(replaceWordsWithNERPOSTags, newList);
			}
		}
		return result;
	}

	/**
	 * Working
	 */
	public void evaluationWithPOSAndNERDictionaryConsiderCategory() {
		resetMetrics();
		//final Map<Category,Integer> truePositiveStatisticForEachCategory = new HashMap<>();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(generateNERDictionary);

		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();

			final String taggedFullTextNER = NERTagger.replaceWordsWithTags(NERTagger.runTagger(fullText), fullText);
			final String taggedFullTextNERPOS = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTagger(taggedFullTextNER), fullText);
			final Map<String, List<Tuple<Category, String>>> groundTruthDataTagged = runNERPOSOnGroundTruth(
					groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : generatePOSDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();

				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);

				final Matcher matcher = pattern.matcher(taggedFullTextNERPOS);

				//final Map<String, List<Tuple<Category, String>>> groundTruthData = new TreeMap<>(
				//		String.CASE_INSENSITIVE_ORDER);
				//groundTruthData.putAll(groundTruthFile.getData());
				while (matcher.find()) {

					final String foundRoleInText = matcher.group(0);

					final List<Tuple<Category, String>> listOfCategory = groundTruthDataTagged.get(foundRoleInText);
					if (listOfCategory == null) {
						precision.addFalsePositive();
					} else {
						final Set<Category> collect = new HashSet<>(
								listOfCategory.stream().map(p -> p.key).collect(Collectors.toList()));
						final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
						if (!intesection.isEmpty()) {
							alreadyFound.add(foundRoleInText);
							precision.addTruePositive();
							recall.addTruePositive();

							//							for(Category cat:intesection){
							//								final Integer integer = truePositiveStatisticForEachCategory.get(cat);
							//								if(integer==null){
							//									truePositiveStatisticForEachCategory.put(cat, 1);
							//								}else{
							//									truePositiveStatisticForEachCategory.put(cat, integer+1);
							//								}
							//							}
							//							
							//							
							List<Tuple<Category, String>> list = groundTruthDataTagged.get(foundRoleInText);
							if(list.size()==1){
								groundTruthDataTagged.remove(foundRoleInText);
							}else{
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthDataTagged.get(foundRoleInText).remove(i);
										break;
									}
								}
							}

						} else {
							precision.addFalsePositive();
						}
					}
				}
			}
			for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERPOSDictionaryConsiderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		//		for(Entry<Category, Integer> entry:truePositiveStatisticForEachCategory.entrySet()){
		//			LOG.info(entry.getKey()+"-" +entry.getValue());
		//		}
		LOG.info("--------------------------------------------");

	}

	public void evaluationWithNERAndThenPOSDictionary() {
		resetMetrics();

		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(roleProvider.getData());

		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();

			final String taggedFullTextNER = NERTagger.replaceWordsWithTags(NERTagger.runTagger(fullText), fullText);
			final Map<String, List<Tuple<Category, String>>> groundTruthDataNERTagged = runNEROnGroundTruth(
					groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : generateNERDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);

				final Matcher matcher = pattern.matcher(taggedFullTextNER);

				//final Map<String, List<Tuple<Category, String>>> groundTruthData = new TreeMap<>(
				//		String.CASE_INSENSITIVE_ORDER);
				//groundTruthData.putAll(groundTruthFile.getData());
				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);

					final String taggedFoundRole = POSTagger
							.replaceWordsWithTags(POSTagger.runPOSTagger(foundRoleInText), foundRoleInText);

					if (!generatePOSDictionary.containsKey(taggedFoundRole)) {
						continue;
					}

					final List<Tuple<Category, String>> listOfCategory = groundTruthDataNERTagged.get(foundRoleInText);
					if (listOfCategory == null) {
						precision.addFalsePositive();
					} else {
						alreadyFound.add(foundRoleInText);
						precision.addTruePositive();
						recall.addTruePositive();
						
						List<Tuple<Category, String>> list = groundTruthDataNERTagged.get(foundRoleInText);
						if(list.size()==1){
							groundTruthDataNERTagged.remove(foundRoleInText);
						}else{
							final Set<Category> collect = new HashSet<>(
									list.stream().map(p -> p.key).collect(Collectors.toList()));
							final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
							if (!intesection.isEmpty()) {
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthDataNERTagged.get(foundRoleInText).remove(i);
										break;
									}
								}
							}
						}
					}
				}
			}
			for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERAndThenPOSDictionary");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		LOG.info("--------------------------------------------");
	}

	@SuppressWarnings("unused")
	private Map<String, List<Tuple<Category, String>>> runPOSOnGroundTruth(
			Map<String, List<Tuple<Category, String>>> groundTruthData) {
		final Map<String, List<Tuple<Category, String>>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		for (Entry<String, List<Tuple<Category, String>>> entry : groundTruthData.entrySet()) {
			final String replaceWordsWithPOSTags = POSTagger
					.replaceWordsWithTagsButNotNER(POSTagger.runPOSTaggerWithNoNER(entry.getKey()), entry.getKey());

			final List<Tuple<Category, String>> list = result.get(replaceWordsWithPOSTags);
			if (list == null) {
				result.put(replaceWordsWithPOSTags, entry.getValue());
			} else {
				List<Tuple<Category, String>> newList = new ArrayList<>(list);
				newList.addAll(entry.getValue());
				result.put(replaceWordsWithPOSTags, newList);
			}
		}
		return result;
	}

	public void evaluationWithNERAndThenPOSDictionaryConsiderCategory() {
		resetMetrics();
		//		final Map<Category,Integer> truePositiveStatisticForEachCategory = new HashMap<>();
		final Map<String, Set<Category>> generateNERDictionary = NERTagger.generateDictionary(roleProvider.getData());
		final Map<String, Set<Category>> generatePOSDictionary = POSTagger
				.generatePOSAndNERDictionary(roleProvider.getData());

		for (GroundTruthFileModified groundTruthFile : groundTruthProvider.getData()) {
			final String fullText = groundTruthFile.getFullContent();
			final Set<String> alreadyFound = new HashSet<>();

			final String taggedFullTextNER = NERTagger.replaceWordsWithTags(NERTagger.runTagger(fullText), fullText);
			final Map<String, List<Tuple<Category, String>>> groundTruthDataNERTagged = runNEROnGroundTruth(
					groundTruthFile.getData());
			for (final Entry<String, Set<Category>> roleEntity : generateNERDictionary.entrySet()) {

				final String dictionaryRole = roleEntity.getKey();
				final Set<Category> dictionaryCategories = roleEntity.getValue();
				final Pattern pattern = Pattern.compile("(?im)" + dictionaryRole);

				final Matcher matcher = pattern.matcher(taggedFullTextNER);

				//final Map<String, List<Tuple<Category, String>>> groundTruthData = new TreeMap<>(
				//		String.CASE_INSENSITIVE_ORDER);
				//groundTruthData.putAll(groundTruthFile.getData());
				while (matcher.find()) {
					final String foundRoleInText = matcher.group(0);
					

					final String taggedFoundRole = POSTagger
							.replaceWordsWithTags(POSTagger.runPOSTagger(foundRoleInText), foundRoleInText);

					if (!generatePOSDictionary.containsKey(taggedFoundRole)) {
						continue;
					}

					final List<Tuple<Category, String>> listOfCategory = groundTruthDataNERTagged.get(foundRoleInText);
					if (listOfCategory == null) {
						precision.addFalsePositive();
					} else {
						final Set<Category> collect = new HashSet<>(
								listOfCategory.stream().map(p -> p.key).collect(Collectors.toList()));
						final Set<Category> intesection = hasIntersection(collect, dictionaryCategories);
						if (!intesection.isEmpty()) {
							alreadyFound.add(foundRoleInText);
							precision.addTruePositive();
							recall.addTruePositive();
							
							List<Tuple<Category, String>> list = groundTruthDataNERTagged.get(foundRoleInText);
							if(list.size()==1){
								groundTruthDataNERTagged.remove(foundRoleInText);
							}else{
								for(int i=0;i<list.size();i++){
									if(intesection.contains(list.get(i).key)){
										groundTruthDataNERTagged.get(foundRoleInText).remove(i);
										break;
									}
								}
							}

							//							for(Category cat:intesection){
							//								final Integer integer = truePositiveStatisticForEachCategory.get(cat);
							//								if(integer==null){
							//									truePositiveStatisticForEachCategory.put(cat, 1);
							//								}else{
							//									truePositiveStatisticForEachCategory.put(cat, integer+1);
							//								}
							//							}
							//							
							//							
							//							List<Tuple<Category, String>> list = groundTruthDataNERTagged.get(foundRoleInText);
							//							if(list.size()==1){
							//								 groundTruthFile.getData().remove(foundRoleInText);
							//							}else{
							//								for(int i=0;i<list.size();i++){
							//									if(intesection.contains(list.get(i).key)){
							//										groundTruthFile.getData().get(foundRoleInText).remove(i);
							//										break;
							//									}
							//								}
							//							}
						} else {
							precision.addFalsePositive();
						}
					}
				}
			}
			for (int i = 0; i < groundTruthFile.getData().keySet().size() - alreadyFound.size(); i++) {
				recall.addFalseNegative();
			}
		}
		LOG.info("evaluationWithNERAndThenPOSDictionaryConsiderCategory");
		LOG.info("Precision= " + precision.getValue());
		LOG.info("Recall= " + recall.getValue());
		LOG.info("FMeasure= " + new FMeasure(precision.getValue(), recall.getValue()).getValue());
		//		for(Entry<Category, Integer> entry:truePositiveStatisticForEachCategory.entrySet()){
		//			LOG.info(entry.getKey()+"-" +entry.getValue());
		//		}
		LOG.info("--------------------------------------------");

	}
}