package evaluationmodifiednewstyle;

import static org.junit.Assert.*;

import org.junit.Test;

import evaluationmodifiednewstyle.GroundTruthFileModifiedNewStyle;
import evaluationmodifiednewstyle.GroundTruthParserModifiedNewStyle;
import model.Position;

public class GroundTruthParserModifiedNewStyleTest {

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
		final GroundTruthFileModifiedNewStyle parseText = GroundTruthParserModifiedNewStyle.parseText(test);
		assertEquals(4,parseText.getRoles().size());
		
		assertEquals(0, parseText.getRoles().get(0).getRolePhasePosition().getStartIndex());
		assertEquals(6, parseText.getRoles().get(0).getRolePhasePosition().getEndIndex());
		
		assertEquals(7, parseText.getRoles().get(1).getRolePhasePosition().getStartIndex());
		assertEquals(13, parseText.getRoles().get(1).getRolePhasePosition().getEndIndex());
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
		final GroundTruthFileModifiedNewStyle parseText = GroundTruthParserModifiedNewStyle.parseText(test);
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
		final GroundTruthFileModifiedNewStyle parseText = GroundTruthParserModifiedNewStyle.parseText(test);
		assertEquals(1,parseText.getRoles().size());
		
		assertEquals(9, parseText.getRoles().get(0).getRolePhasePosition().getStartIndex());
		assertEquals(15, parseText.getRoles().get(0).getRolePhasePosition().getEndIndex());
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
		final GroundTruthFileModifiedNewStyle parseText = GroundTruthParserModifiedNewStyle.parseText(test);
		assertEquals(1,parseText.getRoles().size());
		
		assertEquals(9, parseText.getRoles().get(0).getRolePhasePosition().getStartIndex());
		assertEquals(15, parseText.getRoles().get(0).getRolePhasePosition().getEndIndex());
	}
	
	@Test
	public void testGetTokenNumber(){
		final String test = "I am    a student in KIT University";
		Position result = GroundTruthParserModifiedNewStyle.getTokenNumber(test, new Position(10,23));
		assertEquals(4,result.getStartIndex());
		assertEquals(6,result.getEndIndex());
		
		result = GroundTruthParserModifiedNewStyle.getTokenNumber(test, new Position(0,1));
		assertEquals(1,result.getStartIndex());
		assertEquals(1,result.getEndIndex());
		
		result = GroundTruthParserModifiedNewStyle.getTokenNumber(test, new Position(10,17));
		assertEquals(4,result.getStartIndex());
		assertEquals(4,result.getEndIndex());
		
		result = GroundTruthParserModifiedNewStyle.getTokenNumber(test, new Position(10,17));
		assertEquals(4,result.getStartIndex());
		assertEquals(4,result.getEndIndex());
		
		final String test2 = "WANT to peek at a family Web site crammed with murder, treachery, blood-lust, centuries-long feuds, torture, insanity and whole dungeonfuls of assorted other skeletons? The British monarchy is just the ticket (www.royal.gov.uk).  Not the current entries, of course. Since the royal family entered cyberspace in 1997 to help satisfy the public's insatiable curiosity, its official Web realm has compiled a ream's worth of stars and garters, palaces and Plantagenets. But there is more on the site about the two-penny Tyrian plum stamp of 1910 in the royal philatelic collection than about Sarah Ferguson. (Only one canceled copy of each is known to exist.)  The pages devoted to Diana, Princess of Wales, are suitably prominent, reverential and respectful. The pages detailing a typical day in the life of the queen depict a very wealthy, very earnest, very busy woman. But the mountains of minutiae seem to highlight the obvious omissions, and to make them more poignant. Duty. Dedication. Few tears. Little joy.  Royal Insight, the site's online magazine, highlights important engagements of the month. It will not be mistaken for a tabloid. (May's feature: visiting the Chelsea flower show.) There is enough data about castles, remodeling, acreage, equerries, swan upping, royal mews and queen's beasts to put even the most ardent monarchist to sleep.  The casual viewer who scans the site quickly for the trivia will unearth some gems:  Prince William began his education at Mrs. Mynor's Nursery School in September 1985, at age 3.  The last Welsh Prince of Wales was Llywelyn ap Gruffyd (1246-82).  Victoria's oldest child, Victoria, the Princess Royal, was the mother of Kaiser Wilhelm II.  The royal train has two locomotives. They are named Prince William and Prince Henry.  The royal stamp collection (beloved especially by George V) includes what is believed to be the personal stamp album of the murdered Czarevitch Aleksei, heir to the Russian throne. The brightly illustrated child's album was stolen by a Bolshevik soldier and sold to an employee of a British company in the Soviet Union.  The Britannia was the 83rd royal yacht.  Elizabeth I attended the premiere of ''A Midsummer Night's Dream.''  George III's interest in botany earned him the nickname Farmer George. (No mention of the nicknames he was given on this side of the pond.)  The site also has some unintentionally funny writing. ''George III is widely remembered for two things: losing the American colonies and going mad. This is far from the whole truth.''  The best part of the royal site is the deep past, starting with the Anglo-Saxon kings of Wessex. There are longer profiles of notable monarchs like Alfred the Great, William the Conqueror, Henry VIII (who wrote a best seller defending the Roman Catholic Church, before all that marital unpleasantness changed things) and Charles I (he stammered). As a capsule British history, the site isn't bad, as long as you don't look for too much information on how the common people lived.  But for incomparable mayhem, nothing beats the history of the Scottish crown. Here is a sample from 962 to 1005: Dubh was killed by Culen who was killed by Riderch, king of Strathclyde, whose daughter he had seized. He was succeeded by Kenneth II, Dubh's brother, who killed Culen's brother and was killed by Culen's son, Constantine. Constantine's reign was short; he was killed, probably by Kenneth III. Kenneth was killed and succeeded by Malcolm II.  The glory of Britain isn't its pomp, but its language and its ideals. The speeches of some of its monarchs ring out from the site.  Elizabeth I to her army on the eve of the Spanish Armada: ''I know I have the body of a weak, feeble woman; but I have the heart and stomach of a king -- and of a king of England, too.''  James I proclaims an attitude: ''Kings are justly called gods, for that they exercise a manner of resemblance of divine power on earth.''  A member of Parliament at the time of the Glorious Revolution of 1688, on the importance of making William and Mary and future monarchs financially dependent on the legislative body: ''When princes have not needed money, they have not needed us.''";
		result = GroundTruthParserModifiedNewStyle.getTokenNumber(test2, new Position(169,188));
		assertEquals(25,result.getStartIndex());
		assertEquals(27,result.getEndIndex());
		
		result = GroundTruthParserModifiedNewStyle.getTokenNumber(test2, new Position(685,701));
		assertEquals(25,result.getStartIndex());
		assertEquals(27,result.getEndIndex());
	}
}
