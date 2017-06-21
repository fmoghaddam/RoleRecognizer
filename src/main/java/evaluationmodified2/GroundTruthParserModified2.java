package evaluationmodified2;

import java.io.ByteArrayInputStream;
import java.io.File;
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

import evaluation.Tuple;

public class GroundTruthParserModified2 {

	private final static List<Tuple<Integer, Integer>> allPositions = new ArrayList<>();

	public static GroundTruthFileModified2 parse(String fileName) {
		try {
			final File fXmlFile = new File(fileName);
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			final GroundTruthFileModified2 groundTruthFile = new GroundTruthFileModified2();

			final Node docNode = doc.getChildNodes().item(0);
			final Node timeNode = docNode.getChildNodes().item(1);
			final Node titleNode = docNode.getChildNodes().item(3);
			final Node contentNode = docNode.getChildNodes().item(5);

			groundTruthFile.setFullContent(contentNode.getTextContent());
			groundTruthFile.setTime(timeNode.getTextContent());
			groundTruthFile.setTitle(titleNode.getTextContent());

			if (contentNode.hasChildNodes()) {
				int tagNumber = 0;
				for (int i = 0; i < contentNode.getChildNodes().getLength(); i++) {
					final Node roleNode = contentNode.getChildNodes().item(i);

					String rolePhrase = null;
					String headRole = null;
					if(i%2!=0){
						tagNumber++;
					}
					if (roleNode.getNodeType() == Node.ELEMENT_NODE) {
						Map<String, String> attributes = new HashMap<>();
						rolePhrase = roleNode.getTextContent();

						final Tuple<Integer, Integer> positions = getStartAndEndPositions(rolePhrase,
								contentNode.getTextContent(),tagNumber);
						allPositions.add(positions);
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
						groundTruthFile.addRole(rolePhrase, headRole, positions.key, positions.value, attributes);
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

	private static Tuple<Integer, Integer> getStartAndEndPositions(final String rolePhrase,final String textContent,int tagNumber) {
		final Pattern pattern = Pattern.compile("(?)" + rolePhrase);
		final Matcher matcher = pattern.matcher(textContent);
		int localTagNumber = 0;
		while (matcher.find()) {
			localTagNumber++;
			if(localTagNumber<tagNumber){
				continue;
			}
			boolean overLapFlag = false;
			final Tuple<Integer, Integer> candicatePosition = new Tuple<>(matcher.start(), matcher.end());
			for (final Tuple<Integer, Integer> p : allPositions) {
				if (hasOverLap(p, candicatePosition)) {
					overLapFlag=true;
					break;
				}
			}
			if(overLapFlag){
				continue;
			}
			return candicatePosition;
		}
		return null;
	}

	private static boolean hasOverLap(Tuple<Integer, Integer> p, Tuple<Integer, Integer> candicatePosition) {
		if (candicatePosition.key <= p.value && candicatePosition.key >= p.key
				|| candicatePosition.value <= p.value && candicatePosition.value >= p.key) {
			return true;
		}
		return false;
	}

	public static GroundTruthFileModified2 parseText(String text) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(text.getBytes("utf-8"))));

			doc.getDocumentElement().normalize();

			final GroundTruthFileModified2 groundTruthFile = new GroundTruthFileModified2();

			final Node docNode = doc.getChildNodes().item(0);
			final Node timeNode = docNode.getChildNodes().item(0);
			final Node titleNode = docNode.getChildNodes().item(1);
			final Node contentNode = docNode.getChildNodes().item(2);

			groundTruthFile.setFullContent(contentNode.getTextContent());
			groundTruthFile.setTime(timeNode.getTextContent());
			groundTruthFile.setTitle(titleNode.getTextContent());

			if (contentNode.hasChildNodes()) {
				int tagNumber = 0;
				for (int i = 0; i < contentNode.getChildNodes().getLength(); i++) {
					final Node roleNode = contentNode.getChildNodes().item(i);

					String rolePhrase = null;
					String headRole = null;					

					
					if (roleNode.getNodeType() == Node.ELEMENT_NODE) {
						tagNumber++;
						Map<String, String> attributes = new HashMap<>();
						rolePhrase = roleNode.getTextContent();

						final Tuple<Integer, Integer> positions = getStartAndEndPositions(rolePhrase,
								contentNode.getTextContent(),tagNumber);
						allPositions.add(positions);
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
						groundTruthFile.addRole(rolePhrase, headRole, positions.key, positions.value, attributes);
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
