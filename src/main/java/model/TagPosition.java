package model;

public class TagPosition extends Position {
	private final String tag;

	public TagPosition(String tag, int startIndex, int endIndex) {
		super(startIndex, endIndex);
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return "TagPostion [tag=" + tag + ", toString()=" + super.toString() + "]";
	}

	public boolean hasOverlap(Position otherPosition) {
		if (this.getStartIndex() <= otherPosition.getStartIndex()
				&& this.getEndIndex() >= otherPosition.getEndIndex()) {
			return true;
		} else if (otherPosition.getStartIndex() >= this.getStartIndex()
				&& otherPosition.getStartIndex() <= this.getEndIndex()) {
			return true;
		} else if (otherPosition.getEndIndex() >= this.getStartIndex()
				&& otherPosition.getEndIndex() <= this.getEndIndex()) {
			return true;
		} else if (this.getStartIndex() >= otherPosition.getStartIndex()
				&& this.getStartIndex() <= otherPosition.getEndIndex()) {
			return true;
		} else if (this.getEndIndex() >= otherPosition.getStartIndex()
				&& this.getEndIndex() <= otherPosition.getEndIndex()) {
			return true;
		}
		return false;
	}

	public boolean contains(Position othrePosition) {
		if(this.getStartIndex()<=othrePosition.getStartIndex() && this.getEndIndex()>=othrePosition.getEndIndex()){
			return true;
		}else{
			return false;
		}
	}

	public int getLength() {
		return getEndIndex()-getStartIndex();
	}
}

