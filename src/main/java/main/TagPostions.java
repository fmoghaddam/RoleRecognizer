package main;

import java.util.ArrayList;
import java.util.List;

public class TagPostions {

	private final List<TagPostion> positions = new ArrayList<>();

	public void add(final TagPostion tp) {
		if (!alreadyExist(tp)) {
			positions.add(tp);
		}
	}

	public boolean alreadyExist(TagPostion tp) {
		for (TagPostion tagPosition : positions) {
			if (tagPosition.getStartIndex() <= tp.getStartIndex() && tagPosition.getEndIndex() >= tp.getEndIndex()) {
				return true;
			}
		}
		return false;
	}

	public void reset() {
		positions.clear();
	}
}
