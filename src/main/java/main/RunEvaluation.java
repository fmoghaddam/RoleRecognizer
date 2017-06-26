package main;

import java.io.IOException;

import evaluationmodified.EvaluatorFullText;
import evaluationmodifiednewstyle.EvaluatorFullTextNewStyle;

public class RunEvaluation {

	public static void main(String[] args) throws IOException {
//		Evaluator eval = new Evaluator();
		//eval.containEvaluationWithOriginalDictionary();
//		eval.exactMatchEvaluationWithOriginalDictionary();
//		eval.evaluationWithNERDictionary();
//		eval.evaluationWithPOSAndNERDictionary();
//		eval.evaluationWithPOSDictionary();
		
//		EvaluatorFullText eval = new EvaluatorFullText();
////		
//		eval.exactMatchEvaluationWithOriginalDictionary();
//		eval.exactMatchEvaluationWithOriginalDictionaryConsdeirCategory();
////		
//		eval.evaluationWithNERDictionary();
//		eval.evaluationWithNERDictionaryConsiderCategory();
////		
//		eval.evaluationWithPOSAndNERDictionary();
//		eval.evaluationWithPOSAndNERDictionaryConsiderCategory();
//		
//		eval.evaluationWithNERAndThenPOSDictionary();
//		eval.evaluationWithNERAndThenPOSDictionaryConsiderCategory();
//		
//		
//		
//		
//		System.out.println(POSTagger.runPOSTagger2("I am an stupid guy, person"));
//		System.out.println(POSTagger.replaceWordsWithTags(POSTagger.runPOSTagger2("I am an stupid guy, person"),"I am an stupid guy, person"));
		
//		System.out.println(POSTagger.runPOSTaggerWithNoNER("I am an <LOCATION> guy, person"));
//		System.out.println(POSTagger.replaceWordsWithTagsButNotNER(POSTagger.runPOSTaggerWithNoNER("I am an <LOCATION> guy, person"),"I am an <LOCATION> guy, person"));
		
//		String s = "Monarchy";
//		System.err.println(s.toLowerCase().contains("monarch".toLowerCase()));
		
		final EvaluatorFullTextNewStyle eval = new EvaluatorFullTextNewStyle();
		eval.exactMatchEvaluationWithOriginalDictionary();
		eval.exactMatchEvaluationWithOriginalDictionaryConsiderCategory();
		eval.evaluationWithNERDictionary();
		eval.evaluationWithNERDictionaryConsiderCategory();
		
		
	}

}
