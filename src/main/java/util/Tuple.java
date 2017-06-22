package util;
public class Tuple<X, Y> { 
	public final X key; 
	public final Y value; 
	
	public Tuple(X x, Y y) { 
		this.key = x; 
		this.value = y; 
	}
	
	@Override
	public String toString() {
		return "Tuple [key=" + key + ", value=" + value + "]";
	} 

} 
