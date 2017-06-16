package evaluationmodified;

import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

import java.util.Set;

import main.Category;

public abstract class GroundTruchProviderModified {
	private static Logger LOG = Logger.getLogger(GroundTruchProviderModified.class);
	protected  Set<GroundTruthFileModified> data = new HashSet<>();
	
	protected void loadDate() {
	
	};
	
	protected Map<String, Set<Category>> extractInformation(final String dummyData) {
		return null;
	}
	
	protected void print() {
		for(GroundTruthFileModified entry: data){
			LOG.info(entry);
		}
	}

	public Set<GroundTruthFileModified> getData() {
		return data;
	}

}
