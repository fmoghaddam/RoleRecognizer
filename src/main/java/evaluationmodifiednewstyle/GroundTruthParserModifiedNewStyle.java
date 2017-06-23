package evaluationmodifiednewstyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import model.Position;

public class GroundTruthParserModifiedNewStyle {

	private final static List<Position> allPositions = new ArrayList<>();

	public static GroundTruthFileModifiedNewStyle parse(String fileName) {
		try {
			final File fXmlFile = new File(fileName);
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			final GroundTruthFileModifiedNewStyle groundTruthFile = new GroundTruthFileModifiedNewStyle();

			final Node docNode = doc.getChildNodes().item(0);
			final Node timeNode = docNode.getChildNodes().item(1);
			final Node titleNode = docNode.getChildNodes().item(3);
			final Node contentNode = docNode.getChildNodes().item(5);

			groundTruthFile.setFullContent(contentNode.getTextContent().replaceAll("[\\t\\n\\r]"," ").trim());
			groundTruthFile.setTime(timeNode.getTextContent());
			groundTruthFile.setTitle(titleNode.getTextContent());

			if (contentNode.hasChildNodes()) {
				for (int i = 0; i < contentNode.getChildNodes().getLength(); i++) {
					final Node roleNode = contentNode.getChildNodes().item(i);

					String rolePhrase = null;
					String headRole = null;
					Map<String, String> attributes = new HashMap<>();
					if (roleNode.getNodeType() == Node.ELEMENT_NODE && isValidTag(roleNode.getNodeName())) {
						rolePhrase = roleNode.getTextContent();

						final Position position = getStartAndEndPositions(rolePhrase,
								groundTruthFile.getFullContent());
						if(position==null){
							throw new IllegalArgumentException("Position can not be null. RolePhrase=" +rolePhrase+" filename="+fileName);
						}
						allPositions.add(position);
						if (roleNode.hasChildNodes()) {
							for (int j = 0; j < roleNode.getChildNodes().getLength(); j++) {
								final Node headRoleNode = roleNode.getChildNodes().item(j);
								if (headRoleNode.getNodeType() != Node.ELEMENT_NODE || !isValidTag(headRoleNode.getNodeName())) {
									continue;
								}
								headRole = headRoleNode.getTextContent();

								if (headRoleNode.hasAttributes()) {
									final NamedNodeMap nodeMap = headRoleNode.getAttributes();
									attributes = new HashMap<>();
									for (int k = 0; k < nodeMap.getLength(); k++) {
										final Node node = nodeMap.item(k);
										attributes.put(node.getNodeName(), node.getNodeValue());
									}
								}
							}
						}
						groundTruthFile.addRole(rolePhrase, headRole, position, attributes,getTokenNumber(groundTruthFile.getFullContent(),position));
					}
				}
			}
			allPositions.clear();
			return groundTruthFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	static Position getTokenNumber(String textContent, Position position) {
		int tokenCounter = 0 ;
		int currentPosition = 0;
		for (String token : textContent.split("\\s+")) {
			tokenCounter++;
			currentPosition = textContent.indexOf(token, currentPosition);
//			System.out.println(position.getStartIndex() + " " + currentPosition);
			if(currentPosition==position.getStartIndex()){
				final String subString = textContent.substring(currentPosition);
				int endTokenCounter = tokenCounter;
				for(String newToken: subString.split("\\s+")){
//					System.err.println(position.getEndIndex() + " " + (textContent.indexOf(newToken, currentPosition)+clarifyToken(newToken).length()));
					currentPosition = textContent.indexOf(newToken, currentPosition)+clarifyToken(newToken).length();
					if(currentPosition==position.getEndIndex()){
						return new Position(tokenCounter, endTokenCounter);
					}else{
						endTokenCounter++;
					}
				}
			}
			currentPosition = currentPosition+token.length()-1;
		}
		
		return new Position(-1, -1);
	}

	static Position getTokenNumberstanfordTokenizer(String textContent, Position position) {
		int tokenCounter = 0 ;
		int currentPosition = 0;
		 PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new StringReader(textContent),
	             new CoreLabelTokenFactory(), "");
	     while (ptbt.hasNext()) {
	       CoreLabel label = ptbt.next();
	       String token = label.originalText();
	       tokenCounter++;
			currentPosition = textContent.indexOf(token, currentPosition);
//			System.out.println(position.getStartIndex() + " " + currentPosition);
			if(currentPosition==position.getStartIndex()){
				final String subString = textContent.substring(currentPosition);
				int endTokenCounter = tokenCounter;
				for(String newToken: subString.split("\\s+")){
//					System.err.println(position.getEndIndex() + " " + (textContent.indexOf(newToken, currentPosition)+clarifyToken(newToken).length()));
					currentPosition = textContent.indexOf(newToken, currentPosition)+clarifyToken(newToken).length();
					if(currentPosition==position.getEndIndex()){
						return new Position(tokenCounter, endTokenCounter);
					}else{
						endTokenCounter++;
					}
				}
			}
			currentPosition = currentPosition+token.length()-1;
	     }
		
		return new Position(-1, -1);
	}
	private static String clarifyToken(String newToken) {
		final char ch = newToken.charAt(newToken.length()-1);
		if ((ch>='A' && ch<='Z') || (ch>='a' && ch<='z')) {
			return newToken;
		}else{
			return newToken.substring(0, newToken.length()-1);
		}
	}

	private static boolean isValidTag(String nodeName) {
		if(nodeName.equals("ROLE") || nodeName.equals("HEADROLE")){
			return true;
		}
		return false;
	}

	private static Position getStartAndEndPositions(final String rolePhrase, final String textContent) {
		String replecedRolePhrase = rolePhrase.replaceAll("\\(", "\\\\(");
		replecedRolePhrase = replecedRolePhrase.replaceAll("\\)", "\\\\)");
		final Pattern pattern = Pattern.compile("(?i)" +replecedRolePhrase);
		final Matcher matcher = pattern.matcher(textContent);
		while (matcher.find()) {
			boolean overLapFlag = false;
			final Position candicatePosition = new Position(matcher.start(), matcher.end());
			for (final Position p : allPositions) {
				if (hasOverLap(p, candicatePosition)) {
					overLapFlag = true;
					break;
				}
			}
			if (overLapFlag) {
				continue;
			}
			return candicatePosition;
		}
		return null;
	}

	private static boolean hasOverLap(Position p, Position candicatePosition) {
		if (candicatePosition.getStartIndex() <= p.getEndIndex() && candicatePosition.getStartIndex() >= p.getStartIndex()
				|| candicatePosition.getEndIndex() <= p.getEndIndex() && candicatePosition.getEndIndex() >= p.getStartIndex()) {
			return true;
		}
		return false;
	}

	public static GroundTruthFileModifiedNewStyle parseText(String text) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(text.getBytes("utf-8"))));

			doc.getDocumentElement().normalize();

			final GroundTruthFileModifiedNewStyle groundTruthFile = new GroundTruthFileModifiedNewStyle();

			final Node docNode = doc.getChildNodes().item(0);
			final Node timeNode = docNode.getChildNodes().item(0);
			final Node titleNode = docNode.getChildNodes().item(1);
			final Node contentNode = docNode.getChildNodes().item(2);

			groundTruthFile.setFullContent(contentNode.getTextContent());
			groundTruthFile.setTime(timeNode.getTextContent());
			groundTruthFile.setTitle(titleNode.getTextContent());

			if (contentNode.hasChildNodes()) {
				for (int i = 0; i < contentNode.getChildNodes().getLength(); i++) {
					final Node roleNode = contentNode.getChildNodes().item(i);

					String rolePhrase = null;
					String headRole = null;
					Map<String, String> attributes = new HashMap<>();
					if (roleNode.getNodeType() == Node.ELEMENT_NODE && isValidTag(roleNode.getNodeName())) {
						rolePhrase = roleNode.getTextContent();

						final Position position = getStartAndEndPositions(rolePhrase,
								contentNode.getTextContent());
						if(position==null){
							throw new IllegalArgumentException("Position can not be null. RolePhrase=" +rolePhrase);
						}
						allPositions.add(position);
						if (roleNode.hasChildNodes()) {
							for (int j = 0; j < roleNode.getChildNodes().getLength(); j++) {
								final Node headRoleNode = roleNode.getChildNodes().item(j);
								headRole = headRoleNode.getTextContent();

								if (headRoleNode.hasAttributes()) {
									final NamedNodeMap nodeMap = headRoleNode.getAttributes();
									attributes = new HashMap<>();
									for (int k = 0; k < nodeMap.getLength(); k++) {
										final Node node = nodeMap.item(k);
										attributes.put(node.getNodeName(), node.getNodeValue());
									}
								}
							}
						}
						groundTruthFile.addRole(rolePhrase, headRole, position, attributes,getTokenNumber(contentNode.getTextContent(),position));
					}
				}
			}
			allPositions.clear();
			return groundTruthFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
