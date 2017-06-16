package evaluation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.Category;

public class GroundTruthFile {
	
	private String time;
	private String title;
	private Map<Category,List<Tuple<String,String>>> data = new HashMap<>();
	private String fullContent;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public void addData(Category category,String name,String value){
		final List<Tuple<String, String>> tuples = data.get(category);
		if(tuples == null){
			data.put(category, Arrays.asList(new Tuple<String, String>(name, value)));
			
		}else{
			final List<Tuple<String, String>> newTuples = new ArrayList<>(tuples); 
			newTuples.add(new Tuple<String, String>(name,value));
			data.put(category, newTuples);
		}
	}
	
	public Map<Category, List<Tuple<String, String>>> getData() {
		return data;
	}
	public void setData(Map<Category, List<Tuple<String, String>>> data) {
		this.data = data;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFullContent() {
		return fullContent;
	}
	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
	}
	@Override
	public String toString() {
		return "GroundTruthFile [time=" + time + ", title=" + title + ", data=" + data + "]";
	}
	
}
