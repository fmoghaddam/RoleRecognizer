package model;

public enum Category {
	PRESIDENT_TAG("president"),
	POPE_TAG("pope"), 
	KING_TAG("king"), 
	CEO_TAG("ceo"),
	TOPIC_TAG ("topic");

	private String text;

	Category(String text) {
		this.text = text;
	}

	public String text() {
		return text;
	}
	
	public static Category resolve(String text){
		for(Category cat: Category.values()){
			if(cat.text().equals(text.toLowerCase())){
				return cat;
			}
		}
		return null;
	}
}