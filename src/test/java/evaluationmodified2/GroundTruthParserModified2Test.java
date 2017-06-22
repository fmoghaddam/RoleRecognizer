package evaluationmodified2;

import static org.junit.Assert.*;

import org.junit.Test;

public class GroundTruthParserModified2Test {

	@Test
	public void testPositionsIfEverythingIsTag() {
		final String test = "<DOCUMENT>"
				+ "<TIME>10.02.2015</TIME>"
				+ "<TITLE>He He he</TITLE>"
				+ "<CONTENT>"
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"\">Pope</HEADROLE></ROLE> "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"\">Pope</HEADROLE></ROLE> "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"\">Pope</HEADROLE></ROLE> "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"\">Pope</HEADROLE></ROLE> "
				+ "</CONTENT>"
				+ "</DOCUMENT>";
		final GroundTruthFileModified2 parseText = GroundTruthParserModified2.parseText(test);
		assertEquals(4,parseText.getRoles().size());
		
		assertEquals(0, parseText.getRoles().get(0).getStartRolePhrasePositon());
		assertEquals(6, parseText.getRoles().get(0).getEndRolePhrasePositon());
		
		assertEquals(7, parseText.getRoles().get(1).getStartRolePhrasePositon());
		assertEquals(13, parseText.getRoles().get(1).getEndRolePhrasePositon());
	}

	@Test
	public void testPositionsWhenThereIsNoTag() {
		final String test = "<DOCUMENT>"
				+ "<TIME>10.02.2015</TIME>"
				+ "<TITLE>He He he</TITLE>"
				+ "<CONTENT>"
				+ "A Pope A Pope A Pope A Pope A Pope A Pope"
				+ "</CONTENT>"
				+ "</DOCUMENT>";
		final GroundTruthFileModified2 parseText = GroundTruthParserModified2.parseText(test);
		assertEquals(0,parseText.getRoles().size());
	}
	
	@Test
	public void testPositionsWhenThereIsATagAndDifferentNoTag() {
		final String test = "<DOCUMENT>"
				+ "<TIME>10.02.2015</TIME>"
				+ "<TITLE>He He he</TITLE>"
				+ "<CONTENT>"
				+ "The Pope "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"\">Pope</HEADROLE></ROLE> "
				+ "</CONTENT>"
				+ "</DOCUMENT>";
		final GroundTruthFileModified2 parseText = GroundTruthParserModified2.parseText(test);
		assertEquals(1,parseText.getRoles().size());
		
		assertEquals(9, parseText.getRoles().get(0).getStartRolePhrasePositon());
		assertEquals(15, parseText.getRoles().get(0).getEndRolePhrasePositon());
	}
	
	//@Test
	public void testPositionsWhenThereIsATagAndSameNoTag() {
		final String test = "<DOCUMENT>"
				+ "<TIME>10.02.2015</TIME>"
				+ "<TITLE>He He he</TITLE>"
				+ "<CONTENT>"
				+ "A Pope "
				+ "<ROLE>A <HEADROLE entity=\"\" type=\"\">Pope</HEADROLE></ROLE> "
				+ "</CONTENT>"
				+ "</DOCUMENT>";
		final GroundTruthFileModified2 parseText = GroundTruthParserModified2.parseText(test);
		assertEquals(1,parseText.getRoles().size());
		
		assertEquals(9, parseText.getRoles().get(0).getStartRolePhrasePositon());
		assertEquals(15, parseText.getRoles().get(0).getEndRolePhrasePositon());
	}
}
