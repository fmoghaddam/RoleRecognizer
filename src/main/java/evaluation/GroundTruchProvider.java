package evaluation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import java.util.Set;

import main.Category;

public abstract class GroundTruchProvider {
	private static Logger LOG = Logger.getLogger(GroundTruchProvider.class);
	protected  Map<String, Set<Category>> data = new LinkedHashMap<>();
	
	protected void loadDate() {
	
	};
	
	protected Map<String, Set<Category>> extractInformation(final String dummyData) {
		return null;
	}
	
	protected void print() {
		for(Entry<String, Set<Category>> entry: data.entrySet()){
			LOG.info(entry);
		}
	}

	public Map<String, Set<Category>> getData() {
		return data;
	}

}
