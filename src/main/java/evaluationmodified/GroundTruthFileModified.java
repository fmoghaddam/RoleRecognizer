package evaluationmodified;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import evaluation.Tuple;
import main.Category;

public class GroundTruthFileModified {
	
	private String time;
	private String title;
	private Map<String,List<Tuple<Category,String>>> data = new LinkedHashMap<>();
	private String fullContent;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public void addData(String name,Category category,String value){
		final List<Tuple<Category, String>> tuples = data.get(name);
		if(tuples == null){
			data.put(name, Arrays.asList(new Tuple<Category, String>(category, value)));
			
		}else{
			final List<Tuple<Category, String>> newTuples = new ArrayList<>(tuples); 
			newTuples.add(new Tuple<Category, String>(category,value));
			data.put(name, newTuples);
		}
	}
	
	public Map<String, List<Tuple<Category, String>>> getData() {
		final Map<String,List<Tuple<Category,String>>> result = new LinkedHashMap<>();
		for(Entry<String, List<Tuple<Category, String>>> entry:data.entrySet()){
			result.put(entry.getKey(),new ArrayList<>(entry.getValue()));
		}
		return result;
	}
	public void setData(Map<String, List<Tuple<Category, String>>> data) {
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
