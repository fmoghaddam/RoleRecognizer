package evaluation;

import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

import model.Category;

import java.util.Set;

public abstract class GroundTruchProvider {
	private static Logger LOG = Logger.getLogger(GroundTruchProvider.class);
	protected  Set<GroundTruthFile> data = new HashSet<>();
	
	protected void loadDate() {
	
	};
	
	protected Map<String, Set<Category>> extractInformation(final String dummyData) {
		return null;
	}
	
	protected void print() {
		for(GroundTruthFile entry: data){
			LOG.info(entry);
		}
	}

	public Set<GroundTruthFile> getData() {
		return data;
	}

}
