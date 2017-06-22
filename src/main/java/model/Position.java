package model;

public class Position {
	private final int startIndex;
	private final int endIndex;
	
	public Position(int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	@Override
	public String toString() {
		return "Position [startIndex=" + startIndex + ", endIndex=" + endIndex + "]";
	}
	
}
