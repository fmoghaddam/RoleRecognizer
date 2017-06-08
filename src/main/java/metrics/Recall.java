package metrics;

public class Recall implements Metric {
	private double truePositive=0;
	private double falseNegative=0;
	
	public void addTruePositive(){
		truePositive++;
	}
	
	public void addFalseNegative(){
		falseNegative++;
	}
	
	public double getValue(){
		return truePositive/(truePositive+falseNegative);
	}

	@Override
	public String toString() {
		return "Recall [truePositive=" + truePositive + ", falseNegative=" + falseNegative + "]";
	}
	
	
}
