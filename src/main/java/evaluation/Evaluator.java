package evaluation;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import java.util.Set;

import main.Category;
import main.RoleListProvider;
import main.RoleListProviderDummy;
import main.RoleListProviderFileBased;
import metrics.FMeasure;
import metrics.Precision;
import metrics.Recall;

public class Evaluator {

	private static Logger LOG = Logger.getLogger(Evaluator.class);
	
	public void containEvaluation(){
		final Precision precision = new Precision();
		final Recall recall = new Recall();
		
		//final RoleListProvider roleProvider = new RoleListProviderDummy();
		final RoleListProvider roleProvider = new RoleListProviderFileBased();
		roleProvider.loadRoles();
		
		final GroundTruchProvider groundTruthProvider = new GroundTruthProviderDummy();
		groundTruthProvider.loadDate();
		
		for(Entry<String, Set<Category>> entry: groundTruthProvider.getData().entrySet()){
			final String groundTruthRole = entry.getKey();
			final Set<Category> categories = roleProvider.getValues().get(groundTruthRole);
			if(categories != null){
				final Set<Category> newSet = new HashSet<>(categories);
				final Set<Category> groundTruthCategories = entry.getValue();
				final boolean hasIntesection = hasIntersection(newSet,groundTruthCategories);
				if(hasIntesection){
					precision.addTruePositive();
					recall.addTruePositive();
				}else{
					precision.addFalsePositive();
				}				
			}
			else{
				final Set<String> allRoles = roleProvider.getValues().keySet();
				boolean found = false;
				for(String role:allRoles){
					final Pattern pattern = Pattern.compile("(?i)"+ "\\b" + groundTruthRole + "\\b");
					final Matcher matcher = pattern.matcher(role);
					if(matcher.find()){
						precision.addTruePositive();
						recall.addTruePositive();
						found=true;
						break;
					}
				}
				if(!found){
					recall.addFalseNegative();
				}
			}
		}
		
		LOG.info("Precision= "+precision.getValue());
		LOG.info("Recall= "+recall.getValue());
		LOG.info("FMeasure= "+new FMeasure(precision.getValue(), recall.getValue()).getValue());
	}
	
	public void exactMatchEvaluation(){
		final Precision precision = new Precision();
		final Recall recall = new Recall();
		
		final RoleListProvider provider = new RoleListProviderDummy();
		provider.loadRoles();
		
		final GroundTruchProvider groundTruthProvider = new GroundTruthProviderDummy();
		groundTruthProvider.loadDate();
		
		for(Entry<String, Set<Category>> entry: groundTruthProvider.getData().entrySet()){
			final Set<Category> categories = provider.getValues().get(entry.getKey());
			if(categories == null){
				recall.addFalseNegative();
			}
			else{
				final Set<Category> newSet = new HashSet<>(categories);
				final boolean hasIntesection = hasIntersection(newSet,entry.getValue());
				if(hasIntesection){
					precision.addTruePositive();
					recall.addTruePositive();
				}else{
					precision.addFalsePositive();
				}
			}
		}
		
		LOG.info("Precision= "+precision.getValue());
		LOG.info("Recall= "+recall.getValue());
		LOG.info("FMeasure= "+new FMeasure(precision.getValue(), recall.getValue()).getValue());
	}
	
	private static boolean hasIntersection(Set<Category> set1, Set<Category> set2) {
		for(Category cat:set1){
			if(set2.contains(cat)){
				return true;
			}
		}
		return false;
	}

}
