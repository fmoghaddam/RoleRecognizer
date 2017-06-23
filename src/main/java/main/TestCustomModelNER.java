package main;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationOutputter;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.XMLOutputter;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.sequences.PlainTextDocumentReaderAndWriter;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;
import nu.xom.Document;

public class TestCustomModelNER {

	public static void main(String[] args) throws IOException {
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
//		props.put("regexner.mapping", "src/main/resources/role.txt");
//		props.put("regexner.ignorecase", "true");
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//		final String text = new String(Files.readAllBytes(Paths.get("src/main/resources/roletest.txt")));
//
//		//		NERClassifierCombiner classifier = new NERClassifierCombiner(props);
//		//		final String nerTaggedResult = classifier.classifyWithInlineXML(text);
//		//		System.err.println(nerTaggedResult);
//
//		Annotation document = new Annotation(text);
//
//		pipeline.annotate(document);
//
//		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
//
//		//	    Document doc = XMLOutputter.annotationToDoc(document, pipeline);
//		//	    System.out.println( doc.toXML() );
//
//
//		//	    System.err.println(document.toShorterString("KING"));
//		//	    AnnotationOutputter a = new AnnotationOutputter
//
//		//	    StringWriter stringWriter = new StringWriter();
//		//	    pipeline.prettyPrint(document, new PrintWriter(stringWriter));
//		//	    String result = stringWriter.getBuffer().toString();
//		//	    
//		//	    System.err.println(result);
//
//
//		//		AbstractSequenceClassifier<CoreMap> ner = CRFClassifier.getDefaultClassifier(props);
//
//		//			System.out.println(ner.classifyWithInlineXML(text));
//
//		for(CoreMap sentence: sentences) {
//			System.err.println(convertSentenceToXML(sentence));
//			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
//				String word = token.get(TextAnnotation.class);
//				String pos = token.get(PartOfSpeechAnnotation.class);
//				String ne = token.get(NamedEntityTagAnnotation.class);
//				//				if(!ne.equals("O"))
//				System.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
//			}
//		}
		
		DocumentPreprocessor dp = new DocumentPreprocessor("src/main/resources/roletest.txt");
	      for (List<HasWord> sentence : dp) {
	        System.out.println(sentence);
	      }
	      // option #2: By token
	      PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new FileReader("src/main/resources/roletest.txt"),
	              new CoreLabelTokenFactory(), "");
	      while (ptbt.hasNext()) {
	        CoreLabel label = ptbt.next();
	        System.out.println(label);
	      }
	}

	private static String convertSentenceToXML(CoreMap sentence) {
		StringBuilder result = new StringBuilder();
		for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			String word = token.get(TextAnnotation.class);
			String ne = token.get(NamedEntityTagAnnotation.class);
			if(!ne.equals("O")){

			}else{
				result.append(word).append(" ");
			}
		}
		return result.toString();
	}
}
