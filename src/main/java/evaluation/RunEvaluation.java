package evaluation;

import java.io.IOException;

import evaluationmodified.EvaluatorFullText;

public class RunEvaluation {

	public static void main(String[] args) throws IOException {
//		Evaluator eval = new Evaluator();
//		eval.containEvaluationWithOriginalDictionary();
//		eval.exactMatchEvaluationWithOriginalDictionary();
//		eval.evaluationWithNERDictionary();
//		eval.evaluationWithPOSAndNERDictionary();
//		eval.evaluationWithPOSDictionary();
		
		EvaluatorFullText eval = new EvaluatorFullText();
		
		eval.exactMatchEvaluationWithOriginalDictionary();
		eval.exactMatchEvaluationWithOriginalDictionaryConsdeirCategory();
//		
		eval.evaluationWithNERDictionary();
		eval.evaluationWithNERDictionaryConsiderCategory();
//		
		eval.evaluationWithPOSAndNERDictionary();
		eval.evaluationWithPOSAndNERDictionaryConsiderCategory();
		
		eval.evaluationWithNERAndThenPOSDictionary();
		eval.evaluationWithNERAndThenPOSDictionaryConsiderCategory();
		
		
		
		
//		System.out.println(POSTagger.runPOSTagger2("I am an stupid guy, person"));
//		System.out.println(POSTagger.replaceWordsWithTags(POSTagger.runPOSTagger2("I am an stupid guy, person"),"I am an stupid guy, person"));
		
//		System.out.println(POSTagger.runPOSTaggerWithNoNER("I am an <LOCATION> guy, person"));
//		System.out.println(POSTagger.replaceWordsWithTagsButNotNER(POSTagger.runPOSTaggerWithNoNER("I am an <LOCATION> guy, person"),"I am an <LOCATION> guy, person"));
	}

}
