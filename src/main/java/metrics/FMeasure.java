package metrics;

public class FMeasure implements Metric {
	private final double value1;
	private final double value2;
	public FMeasure(double value1, double value2) {
		super();
		this.value1 = value1;
		this.value2 = value2;
	}

	public double getValue(){
		return (2*value1*value2)/(value1+value2);
	}

	@Override
	public String toString() {
		return "FMeasure [value1=" + value1 + ", value2=" + value2 + "]";
	}
}
