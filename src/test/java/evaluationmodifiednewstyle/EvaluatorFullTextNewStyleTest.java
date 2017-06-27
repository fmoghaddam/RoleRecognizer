package evaluationmodifiednewstyle;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.Position;

public class EvaluatorFullTextNewStyleTest {

	//@Test
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
		
		//final Map<Category, Integer> statistics = EvaluatorFullTextNewStyle.printGroundTruthStatistics(new HashSet<>(Arrays.asList(parseText)));
		//assertEquals(2,statistics.get(Category.resolve("CEO".toLowerCase())).intValue());
		//assertEquals(1,statistics.get(Category.resolve("POPE".toLowerCase())).intValue());
		//assertEquals(1,statistics.get(Category.resolve("PRESIDENT".toLowerCase())).intValue());
	}
	
	@Test
	public void testGetTokenNumberStanfordTokenizer(){
		final String test = "I am an student at, KIT queen's";
		Position tokenNumber = GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(test,new Position(0, 1));
		assertEquals(1, tokenNumber.getStartIndex());
		
		tokenNumber = GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(test,new Position(0, 4));
		assertEquals(1, tokenNumber.getStartIndex());
		assertEquals(2, tokenNumber.getEndIndex());
		
		tokenNumber = GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(test,new Position(5, 15));
		assertEquals(3, tokenNumber.getStartIndex());
		assertEquals(4, tokenNumber.getEndIndex());
		
		tokenNumber = GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(test,new Position(16, 18));
		assertEquals(5, tokenNumber.getStartIndex());
		
		tokenNumber = GroundTruthParserModifiedNewStyle.getTokenNumberStanfordTokenizer(test,new Position(24, 29));
		assertEquals(7, tokenNumber.getStartIndex());
		assertEquals(7, tokenNumber.getEndIndex());
	}

}
