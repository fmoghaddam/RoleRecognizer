package main;
//package com.example.vaadintest;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Collection;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Properties;
//
//import org.apache.log4j.Logger;
//
//import com.vaadin.annotations.Theme;
//import com.vaadin.server.VaadinRequest;
//import com.vaadin.shared.ui.label.ContentMode;
//import com.vaadin.ui.Button;
//import com.vaadin.ui.Label;
//import com.vaadin.ui.RichTextArea;
//import com.vaadin.ui.TextArea;
//import com.vaadin.ui.UI;
//import com.vaadin.ui.VerticalLayout;
//
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.pipeline.StanfordCoreNLP;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//import edu.stanford.nlp.trees.GrammaticalStructure;
//import edu.stanford.nlp.trees.PennTreebankLanguagePack;
//import edu.stanford.nlp.trees.Tree;
//import edu.stanford.nlp.trees.TreeCoreAnnotations;
//import edu.stanford.nlp.trees.TreebankLanguagePack;
//import edu.stanford.nlp.trees.TypedDependency;
//import edu.stanford.nlp.util.CoreMap;
//
//@Theme("VaadinTest")
//public class VaadinTestUI extends UI{
//
//	private static final long serialVersionUID = 5924433731101393970L;
//	private static Logger LOG = Logger.getLogger(VaadinTestUI.class);
//	
//	private String beforeText;
//	private String afterText;
//	private TextArea beforeTextArea;
//	//private RichTextArea afterTextArea;
//	
//	private Label result = new Label();
//	private static final Map<String,String> SOV_MAP = new LinkedHashMap<>();
//	
//	@Override
//	protected void init(VaadinRequest request) {
//		final VerticalLayout content = new VerticalLayout();
//		content.setSpacing(true);
//		content.setMargin(true);
//		setContent(content);
//
//		beforeTextArea = new TextArea();
//		beforeTextArea.setImmediate(true);
//		beforeTextArea.setSizeFull();
//		content.addComponent(beforeTextArea);
//
//		final Button run = new Button("Run");
//		run.addClickListener(event -> {
//			beforeText = beforeTextArea.getValue();
//			afterText = beforeText;
//			result.setValue("");
//			runTagger(beforeText);
//
//		});
//		content.addComponent(run);
//
//		//afterTextArea = new RichTextArea();
//		//afterTextArea.setImmediate(true);
//		//afterTextArea.setSizeFull();
//		//afterTextArea.se
//		content.addComponent(result);
//	}
//	private void runTagger(String beforeText) {
//		Properties props = new Properties();
//		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
//		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//		InputStream is = new ByteArrayInputStream(beforeText.getBytes());
//		BufferedReader br = new BufferedReader(new InputStreamReader(is));
//		final List<List<HasWord>> sentences = MaxentTagger.tokenizeText(br);
//		for (final List<HasWord> fullSentence : sentences) {
//			Annotation document =
//					new Annotation(fullSentence.toString());
//			pipeline.annotate(document);
//			for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
//				Tree constituencyParse = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//				TreebankLanguagePack languagePack = new PennTreebankLanguagePack();
//				GrammaticalStructure structure = languagePack.grammaticalStructureFactory().newGrammaticalStructure(constituencyParse);
//				Collection<TypedDependency> typedDependencies = structure.typedDependenciesCollapsed();
//
//				for(TypedDependency td : typedDependencies) {
//					if(td.reln().toString().equals("nsubj")) {
//						int startOfVerbIndex = 6;
//						int endOfVerbIndex = td.toString().indexOf("-");  
//						int startOfSubjectIndex = td.toString().indexOf(", ")+2;
//						int endOfSubjectIndex = td.toString().lastIndexOf("-");
//						String verb = td.toString().substring(startOfVerbIndex,endOfVerbIndex);
//						String subject = td.toString().substring(startOfSubjectIndex,endOfSubjectIndex);
//						SOV_MAP.put(subject ,verb);
//					}
//				}
//			}
//		}
//		
//		for(Entry<String, String> entity: SOV_MAP.entrySet()){
//			LOG.info("Subject: "+entity.getKey() + "\tVerb: "+entity.getValue());
//			afterText = afterText.replaceAll(entity.getKey(), "<SBJ><mark>"+entity.getKey()+"</mark></SBJ>");
//			afterText = afterText.replaceAll(entity.getValue(), "<VER><mark>"+entity.getValue()+"</mark></VER>");
//			System.err.println("Subject: "+entity.getKey() + "\tVerb: "+entity.getValue());
//			System.err.println(afterText);			
//		}
//		
//		result = new Label(afterText, ContentMode.HTML);
//		LOG.info("--------------------------------------------");
//	}	
//}
