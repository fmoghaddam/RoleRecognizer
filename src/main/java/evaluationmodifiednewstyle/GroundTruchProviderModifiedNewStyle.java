package evaluationmodifiednewstyle;

import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

import model.Category;

import java.util.Set;

public abstract class GroundTruchProviderModifiedNewStyle {
	private static Logger LOG = Logger.getLogger(GroundTruchProviderModifiedNewStyle.class);
	protected  Set<GroundTruthFileModifiedNewStyle> role = new HashSet<>();
	
	protected void loadDate() {
	
	};
	
	protected Map<String, Set<Category>> extractInformation(final String dummyData) {
		return null;
	}
	
	protected void print() {
		for(GroundTruthFileModifiedNewStyle entry: role){
			LOG.info(entry);
		}
	}

	public Set<GroundTruthFileModifiedNewStyle> getRoles() {
		return role;
	}

}
