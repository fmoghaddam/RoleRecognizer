package evaluation;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import model.Category;

@Deprecated
public class GroundTruthParser {

	public static GroundTruthFile parse(String fileName){
		try{
			final File fXmlFile = new File(fileName);
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			final GroundTruthFile groundTruthFile = new GroundTruthFile();

			final Node docNode = doc.getChildNodes().item(0);
			final Node timeNode = docNode.getChildNodes().item(1);
			final Node titleNode = docNode.getChildNodes().item(3);
			final Node contentNode = docNode.getChildNodes().item(5);
			
			groundTruthFile.setFullContent(contentNode.getTextContent());
			groundTruthFile.setTime(timeNode.getTextContent());
			groundTruthFile.setTitle(titleNode.getTextContent());
			
			if(contentNode.hasChildNodes()){
				for(int i=0;i<contentNode.getChildNodes().getLength();i++){
					final Node n = contentNode.getChildNodes().item(i);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						final String categoryName = n.getNodeName();
						final Category category = isCategory(categoryName);
						if(category==null){
							continue;
						}
						final String tagValue = n.getTextContent();
						String entityValue = null;
						if(n.hasAttributes()) {
							NamedNodeMap nodeMap = n.getAttributes();
							//Currently I assume there is only 1 element in the list
							//and this element is "entity"
							for (int j = 0; j < nodeMap.getLength(); j++) {
								final Node node = nodeMap.item(j);
								entityValue = node.getNodeValue();
							}
						}
						groundTruthFile.addData(category, tagValue, entityValue);
					}
				}
			}
			return groundTruthFile;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private static Category isCategory(String string) {
		for(Category category: Category.values()){
			if(category.text().equalsIgnoreCase(string)){
				return category;
			}
		}
		return null;
	}

	public static GroundTruthFile parseText(String text) {
		try{
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(text);

			doc.getDocumentElement().normalize();

			final GroundTruthFile groundTruthFile = new GroundTruthFile();

			final Node docNode = doc.getChildNodes().item(0);
			final Node timeNode = docNode.getChildNodes().item(1);
			final Node titleNode = docNode.getChildNodes().item(3);
			final Node contentNode = docNode.getChildNodes().item(5);

			groundTruthFile.setTime(timeNode.getTextContent());
			groundTruthFile.setTitle(titleNode.getTextContent());

			if(contentNode.hasChildNodes()){
				for(int i=0;i<contentNode.getChildNodes().getLength();i++){
					final Node n = contentNode.getChildNodes().item(i);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						final String categoryName = n.getNodeName();
						final Category category = isCategory(categoryName);
						if(category==null){
							continue;
						}
						final String tagValue = n.getTextContent();
						String entityValue = null;
						if(n.hasAttributes()) {
							NamedNodeMap nodeMap = n.getAttributes();
							//Currently I assume there is only 1 element in the list
							//and this element is "entity"
							for (int j = 0; j < nodeMap.getLength(); j++) {
								Node node = nodeMap.item(j);
								entityValue = node.getNodeValue();
							}
						}
						groundTruthFile.addData(category, tagValue, entityValue);
					}
				}
			}
			return groundTruthFile;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}
}
