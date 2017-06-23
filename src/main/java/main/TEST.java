package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import evaluationmodified.GroundTruthProviderFileBasedModified;
import evaluationmodifiednewstyle.EvaluatorFullTextNewStyle;
import evaluationmodifiednewstyle.GroundTruthFileModifiedNewStyle;
import evaluationmodifiednewstyle.GroundTruthParserModifiedNewStyle;
import evaluationmodifiednewstyle.GroundTruthProviderFileBasedModifiedNewStyle;

public class TEST {

	public static void main(String[] args) {
//		Map<String,Integer> m = new HashMap<>();
		
		
		
//		final RoleListProvider provider = new RoleListProviderFileBased();
//		provider.loadRoles();
//		provider.print();

//		String str1 = "Skynet";
//		String str2 = "SkyHigh asdasd asjdgsjad asjdgasjdg ajsdgajdg ajsgdajsdgajsdgjasgdjsagd jasgdjasgd jasgd ";
//		int distance = StringUtils.getLevenshteinDistance(str1, str2);
//		System.out.println("Distance :" + distance);
		
//		StringTokenizer tokenizer = new StringTokenizer("my name is farshad");
//		while(tokenizer.hasMoreTokens()) {
//		    System.out.println(tokenizer.nextToken());
//		}
//		final Map<String,String> map = function();
//		map.put("farshad", "rima");
//		
//		final Map<String,String> map2 = (Map<String, String>) ((HashMap<String, String>)map).clone();
//		
//		System.err.println(map.entrySet());
//		//System.err.println(map2.entrySet());
//		
//		map2.put("farshad", "XXX");
//		
//		System.err.println(map.entrySet());
		//System.err.println(map2.entrySet());
		
		
//		String html = "<PERSON>Barack Obama</PERSON> went to <MISC>German</MISC> president in <LOCATION>Germany</LOCATION>";
//				Document doc = Jsoup.parse(html);
//				Elements links = doc.select("PERSON");
//				for (Element element : links) {
//					System.err.println(element.className());
//					System.err.println(element.html());
//					System.err.println(element.nodeName());
//				}
//				Elements head = doc.select("MISC");
		
//		try {
//
//			File file = new File("groundTruth/file.xml");
//			JAXBContext jaxbContext = JAXBContext.newInstance(GroundTruthFileJaxB.class);
//
//			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//			GroundTruthFileJaxB customer = (GroundTruthFileJaxB) jaxbUnmarshaller.unmarshal(file);
//			System.out.println(customer);
//
//		  } catch (JAXBException e) {
//			e.printStackTrace();
//		  }
		
//		GroundTruthFileJaxB customer = new GroundTruthFileJaxB();
//		  customer.setId(100);
//		  customer.setName("mkyong");
//		  customer.setAge(29);
//
//		  try {
//
//			File file = new File("groundTruth/file.xml");
//			JAXBContext jaxbContext = JAXBContext.newInstance(GroundTruthFileJaxB.class);
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//			// output pretty printed
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//			jaxbMarshaller.marshal(customer, file);
//			jaxbMarshaller.marshal(customer, System.out);
//
//		      } catch (JAXBException e) {
//			e.printStackTrace();
//		      }
		
//		try {
//			final EvaluatorFullTextNewStyle evaluatorFullTextNewStyle = new EvaluatorFullTextNewStyle();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		final GroundTruthProviderFileBasedModifiedNewStyle groundTruthProviderFileBasedModifiedNewStyle = new GroundTruthProviderFileBasedModifiedNewStyle();
		groundTruthProviderFileBasedModifiedNewStyle.loadDate();
//		groundTruthProviderFileBasedModifiedNewStyle.getRoles().forEach(p->{
//			System.out.println(p.getFullContent());
//			p.getRoles().forEach(x->{
//				System.err.println(x.getRolePhasePosition() + "\t" +x.getRolePhaseTokenPosition() + "\t" + x.getRolePhrase() + "\t"+x.getHeadRole());
//			});
//		});
		
//		printMatches("The British monarchy", "The British monarchy");
		
		
//		final GroundTruthFileModifiedNewStyle parse = GroundTruthParserModifiedNewStyle.parse("groundTruth/NEWStyle");
//		parse.getRoles().forEach(p->{
//			System.err.println(p);
//		});
		
	}
	
	public static void printMatches(String text, String regex) {
		
		final String str = "British";
		System.err.println(text.indexOf(str,2));
		System.err.println(text.indexOf(str,2)+str.length());
		
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(text);
	    // Check all occurrences
	    while (matcher.find()) {
	        System.out.print("Start index: " + matcher.start());
	        System.out.print(" End index: " + matcher.end());
	        System.out.println(" Found: " + matcher.group());
	    }
	}
	
}
