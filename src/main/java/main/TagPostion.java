package main;

public class TagPostion {
	private final int startIndex;
	private final int endIndex;

	public TagPostion(int startIndex, int endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	@Override
	public String toString() {
		return "TagPostion [startIndex=" + startIndex + ", endIndex=" + endIndex + "]";
	}
	
}
