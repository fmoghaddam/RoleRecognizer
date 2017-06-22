package util;
public class Triple<X, Y, Z> { 
	public final X v1; 
	public final Y v2;
	public final Z v3;
	public Triple(X x, Y y, Z z) { 
		this.v1 = x; 
		this.v2 = y; 
		this.v3 = z;
	}
	
	@Override
	public String toString() {
		return "Triple [v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + "]";
	}
	
} 
