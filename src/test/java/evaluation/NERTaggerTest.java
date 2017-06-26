package evaluation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import edu.stanford.nlp.ling.CoreLabel;

public class NERTaggerTest {

	@Test
	public void test() {
		final String testString = "But there is more on the site about the two-penny Tyrian plum stamp of 1910 in the royal philatelic collection than about Sarah Ferguson.";
		final String result = NERTagger.classifier.classifyWithInlineXML(testString);
		System.err.println(result);
		System.err.println(NERTagger.replaceWordsWithTags(result, testString));
		
		
	}

}
