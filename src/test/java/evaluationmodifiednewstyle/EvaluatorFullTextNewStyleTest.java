package evaluationmodifiednewstyle;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import model.Category;

public class EvaluatorFullTextNewStyleTest {

	@Test
	public void testNumberOfTagsInAFile() {
		final String test = "<DOCUMENT>"
				+ "<TIME>10.02.2015</TIME>"
				+ "<TITLE>He He he</TITLE>"
				+ "<CONTENT>"
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"CEO\">Pope</HEADROLE></ROLE> "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"CEO\">Pope</HEADROLE></ROLE> "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"POPE\">Pope</HEADROLE></ROLE> "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"PRESIDENT\">Pope</HEADROLE></ROLE> "
				+ "</CONTENT>"
				+ "</DOCUMENT>";
		final GroundTruthFileModifiedNewStyle parseText = GroundTruthParserModifiedNewStyle.parseText(test);
		assertEquals(4,parseText.getRoles().size());
		
		final Map<Category, Integer> statistics = EvaluatorFullTextNewStyle.printGroundTruthStatistics(new HashSet<>(Arrays.asList(parseText)));
		assertEquals(2,statistics.get(Category.resolve("CEO".toLowerCase())).intValue());
		assertEquals(1,statistics.get(Category.resolve("POPE".toLowerCase())).intValue());
		assertEquals(1,statistics.get(Category.resolve("PRESIDENT".toLowerCase())).intValue());
		
		
	}

}
