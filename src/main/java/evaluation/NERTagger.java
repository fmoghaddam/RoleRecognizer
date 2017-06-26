package evaluation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Set;
import java.util.TreeMap;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import model.Category;
import model.NER_TAG;

public class NERTagger {

	final static String model1 = "src/main/resources/nermodel/english.all.3class.distsim.crf.ser.gz";
	final static String model2 = "src/main/resources/nermodel/english.conll.4class.distsim.crf.ser.gz";
	final static String model3 = "src/main/resources/nermodel/english.muc.7class.distsim.crf.ser.gz";
	static NERClassifierCombiner classifier;
	static {
		try {
			classifier = new NERClassifierCombiner(model1, model2, model3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static Map<String, Set<Category>> generateDictionary(Map<String, Set<Category>> originalDictionary) {

		final Map<String, Set<Category>> nerDictinary = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		for (Entry<String, Set<Category>> entry : originalDictionary.entrySet()) {
			final String text = entry.getKey();
			final Set<Category> categories = entry.getValue();
			try {
				final String nerTaggedResult = classifier.classifyWithInlineXML(text);
				final String replaceWordsWithTags = replaceWordsWithTags(nerTaggedResult, text);
				final Set<Category> set = nerDictinary.get(replaceWordsWithTags);
				if (set == null) {
					nerDictinary.put(replaceWordsWithTags, new HashSet<>(categories));
				} else {
					Set<Category> newSet = new HashSet<>(set);
					newSet.addAll(categories);
					nerDictinary.put(replaceWordsWithTags, newSet);
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		return nerDictinary;
	}

	public static String runTagger(String text) {
		try {
			final String nerTaggedResult = classifier.classifyWithInlineXML(text);
			return nerTaggedResult;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String replaceWordsWithTags(String nerTaggedResult, String originalText) {
		String result = new String(originalText);
		final Document doc = Jsoup.parse(nerTaggedResult);
		for (final NER_TAG tag : NER_TAG.values()) {
			final Elements possibleCandidates = doc.select(tag.text);
			for (Element element : possibleCandidates) {
				if (element.html().split(" ").length <= 1) {
					result = result.replaceAll(element.html(), "<" + element.nodeName().toUpperCase() + ">");
				} else {
					StringBuilder temp = new StringBuilder();
					for (int i = 0; i < element.html().split(" ").length; i++) {
						if (i == element.html().split(" ").length - 1) {
							temp.append("<" + element.nodeName().toUpperCase() + ">");
						} else {
							temp.append("<" + element.nodeName().toUpperCase() + ">").append(" ");
						}
					}
					result = result.replaceAll(element.html(), temp.toString());
				}
			}
		}
		return result;
	}
}
