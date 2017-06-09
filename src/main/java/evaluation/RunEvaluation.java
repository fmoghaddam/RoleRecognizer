package evaluation;

import java.io.IOException;

public class RunEvaluation {

	public static void main(String[] args) throws IOException {
		Evaluator eval = new Evaluator();
		eval.containEvaluationWithOriginalDictionary();
		eval.exactMatchEvaluationWithOriginalDictionary();
		eval.evaluationWithNERDictionary();
	}

}
