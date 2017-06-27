package evaluationmodifiednewstyle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.log4j.Logger;

import model.Category;

import java.util.Set;
import java.util.Map.Entry;

public abstract class GroundTruthProviderModifiedNewStyle {
	private static Logger LOG = Logger.getLogger(GroundTruthProviderModifiedNewStyle.class);
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
	
	public void printStatistic(){
		final Map<Category, Integer> statistic = new HashMap<>();
		for (final GroundTruthFileModifiedNewStyle groundTruth : role) {
			groundTruth.getRoles().forEach(p -> {
				final Category resolveCategory = Category.resolve(p.getXmlAttributes().get("type").toLowerCase());
				final Integer value = statistic.get(resolveCategory);
				if (value == null) {
					statistic.put(resolveCategory, 1);
				} else {
					statistic.put(resolveCategory, value + 1);
				}
			});
		}
		LOG.info("-------------------------------------");
		for (Entry<Category, Integer> entry : statistic.entrySet()) {
			LOG.info(entry.getKey() + "==" + entry.getValue());
		}
		LOG.info("-------------------------------------");
		//return statistic;
	}

	public void printStatisticRolePhrase(){
		final Map<Category, Map<String,Integer>> statistic = new HashMap<>();
		for (final GroundTruthFileModifiedNewStyle groundTruth : role) {
			groundTruth.getRoles().forEach(p -> {
				final Category resolveCategory = Category.resolve(p.getXmlAttributes().get("type").toLowerCase());
				final Map<String, Integer> value = statistic.get(resolveCategory);
				final String rolePhrase = p.getRolePhrase().toLowerCase();
				if (value == null) {
					final Map<String,Integer> newMap = new HashMap<>();
					newMap.put(rolePhrase,1);
					statistic.put(resolveCategory, newMap);
				} else {
					
					final Integer integer = value.get(rolePhrase);
					if(integer==null){
						value.put(rolePhrase, 1);
					}else{
						value.put(rolePhrase, integer+1);	
					}
					statistic.put(resolveCategory, value);
				}
			});
		}
		LOG.info("-------------------------------------");
		for (Entry<Category, Map<String, Integer>> entry : statistic.entrySet()) {
			LOG.info(entry.getKey() + "==" + entry.getValue().size());
			for(Entry<String, Integer> ent:entry.getValue().entrySet()){
				LOG.info(ent.getKey() + "--" + ent.getValue());
			}
		}
		LOG.info("-------------------------------------");
	}
}
