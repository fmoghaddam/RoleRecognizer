package metrics;

public class Precision implements Metric {

	private double truePositive=0;
	private double falsePositive=0;
	
	public void addTruePositive(){
		truePositive++;
	}
	
	public void addFalsePositive(){
		falsePositive++;
	}
	
	public double getValue(){
		return truePositive/(truePositive+falsePositive);
	}

	@Override
	public String toString() {
		return "Precision [truePositive=" + truePositive + ", falsePositive=" + falsePositive + "]";
	}

	@Override
	public void reset() {
		truePositive=0;
		falsePositive=0;
	}
	
}
