package evaluation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Category;
import model.TagPostion;

@Deprecated
public class GroundTruthProviderDummy extends GroundTruchProvider {

	@Override
	public void loadDate() {
		final String dummyData = "I am <PRESIDENT entity=\"Barack_Obama\">president</PRESIDENT> of the<CEO entity=\"\">co-founder</CEO> US."; 
		data.addAll(extractXmlInformation(dummyData));
	}

	private Set<GroundTruthFile> extractXmlInformation(String text){
		return new HashSet<>(Arrays.asList(GroundTruthParser.parseText(text)));
	}
	
	@Override
	protected Map<String, Set<Category>> extractInformation(String text) {
		final Map<String, Set<Category>> result = new LinkedHashMap<>();
		final Pattern pattern = Pattern.compile("(?i)<.*?>");
		final Matcher matcher = pattern.matcher(text);
		TagPostion tagPositionStart = new TagPostion("",0,0);
		TagPostion tagPositionEnd = new TagPostion("",0,0);
		while (matcher.find()) {
			final String tag = matcher.group(0);
			if(!tag.contains("/")){
				tagPositionStart = new TagPostion(tag, matcher.start(), matcher.end());
			}else{
				tagPositionEnd = new TagPostion(tag, matcher.start(), matcher.end());
				final String role = text.substring(tagPositionStart.getEndIndex(), tagPositionEnd.getStartIndex());
				final String category = tagPositionStart.getTag().substring(1,tagPositionStart.getTag().length()-1).toLowerCase();
				final Set<Category> set = result.get(role);
				if(set == null){
					final Set<Category> newSet= new HashSet<>();
					newSet.add(Category.resolve(category));
					result.put(role,newSet);
				}else{
					set.add(Category.resolve(category));
					result.put(role,new HashSet<>(set));
				}
			}
			
		}
		return result;
	}
}
