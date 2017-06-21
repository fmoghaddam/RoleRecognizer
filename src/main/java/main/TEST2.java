package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class TEST2 {
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
	 
	    StringReader strReader = new StringReader("<DOCUMENT><TIME>January 11, 1989</TIME><TITLE>Bush to Attend Hirohito Rites</TITLE><CONTENT>WASHINGTON, Jan. 10— <ROLE><HEADROLE entity=\"George_H._W._Bush\" type=\"PRESIDENT\">President-elect</HEADROLE> George Bush will </ROLE>lead a United States delegation at the funeral of Emperor Hirohito of Japan on Feb. 24, a Bush aide said today. Mr. Bush is to be inaugurated on Jan. 20, and the trip to Japan is likely to be Mr. Bush's first visit abroad as <ROLE> A <HEADROLE entity=\"George_H._W._Bush\" type=\"PRESIDENT\">President</HEADROLE></ROLE>.</CONTENT></DOCUMENT>");
	    MyReader reader = new MyReader(strReader);
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser parser = factory.newSAXParser();
	    XMLReader xmlreader = parser.getXMLReader();
	    Map<String,List<Integer>> startMap = new HashMap<>();
	    Map<String,List<Integer>> endMap = new HashMap<>();
	    Map<String,List<Integer>> startLineMap = new HashMap<>();
	    Map<String,List<Integer>> endLineMap = new HashMap<>();

	    DefaultHandler handler = new DefaultHandler() {

	        @Override
	        public void endElement(String uri, String localName, String qName) throws SAXException {
	            super.endElement(uri, localName, qName); //To change body of generated methods, choose Tools | Templates.
	            List<Integer> l = endMap.get(qName);
	            if(null == l) {
	                l = new ArrayList<>();
	            }
	            l.add(reader.getPos());
	            endMap.put(qName, l);
	            List<Integer> ll = endLineMap.get(qName);
	            if(null == ll) {
	                ll= new ArrayList<>();
	            }
	            ll.add(reader.getLine());
	            endLineMap.put(qName, ll);
	        }

	        @Override
	        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	            super.startElement(uri, localName, qName, attributes); //To change body of generated methods, choose Tools | Templates.
	            List<Integer> l = startMap.get(qName);
	            if(null == l) {
	                l = new ArrayList<>();
	            }
	            l.add(reader.getPos());
	            startMap.put(qName, l);
	            List<Integer> ll = startLineMap.get(qName);
	            if(null == ll) {
	                ll= new ArrayList<>();
	            }
	            ll.add(reader.getLine());
	            startLineMap.put(qName, ll);
	        }
	    };
	    xmlreader.setContentHandler(handler);
	    xmlreader.parse(new InputSource(reader));
	    System.out.println("startMap = " + startMap);
	    System.out.println("endMap = " + endMap);
	    System.out.println("startLineMap = " + startLineMap);
	    System.out.println("endLineMap = " + endLineMap);
	    
	}
}


