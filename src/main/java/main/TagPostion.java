package main;

public class TagPostion {
	private final String tag;
	private final int startIndex;
	private final int endIndex;

	public TagPostion(String tag, int startIndex, int endIndex) {
		this.tag = tag;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return "TagPostion [tag=" + tag + ", startIndex=" + startIndex + ", endIndex=" + endIndex + "]";
	}
}
