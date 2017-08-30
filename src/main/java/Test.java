import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import main.RoleListProvider;
import main.RoleListProviderDummy;
import main.RoleTagger;
import model.Category;
import model.NerTag;
import model.TagPosition;
import model.TagPositions;
import util.ColorUtil;
import util.MapUtil;
import util.NERTagger;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final RoleListProvider provider = new RoleListProviderDummy();
		provider.loadRoles();
		annotateTextWihtNER("president of United States",provider.getData());
	}

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RoleTagger.class);
	private final static TagPositions tagPositions = new TagPositions();

	private static String annotateTextWihtNER(String text, Map<String, Set<Category>> map) {
		String result = new String(text);
		
		final Map<Integer, NerTag> nerStatistic = NERTagger.nerXmlParser(NERTagger.runTaggerXML(result));		
		final String resultNer = NERTagger.runTaggerString(result);
		
		final Map<String, Set<Category>> generatedNerDictionary = generateNerDictionary(map);
		
		for (final Entry<String, Set<Category>> roleEntity : generatedNerDictionary.entrySet()) {
			final List<Category> roleCategory = new ArrayList<>(roleEntity.getValue());
			
			final String role = roleEntity.getKey().replaceAll("\\.", "\\\\.");
			if(role.charAt(0)=='<' && role.charAt(role.length()-1)=='>'){
				continue;
			}
			if(role.equalsIgnoreCase("the <LOCATION>") || 
					role.equalsIgnoreCase("The <ORGANIZATION>")){
				continue;
			}
			String regexPattern = "(?im)";
			if(role.charAt(0)!='<'){
				regexPattern +="\\b";
			}
			regexPattern +=role;
			if(role.charAt(role.length()-1)!='>'){
				regexPattern +="\\b";
			}
			
			final Pattern pattern = Pattern.compile("(?im)" +regexPattern);
			final Matcher matcher = pattern.matcher(resultNer);
			final Set<String> visitedRoles = new HashSet<>();
			while (matcher.find()) {
				final String nerRole = matcher.group(0);
				TagPosition tp = new TagPosition(nerRole,matcher.start(), matcher.end());
				tp = convertPosition(tp,nerStatistic);
				final String nativeRole = result.substring(tp.getStartIndex(), tp.getEndIndex());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}
				if (visitedRoles.contains(nerRole)) {
					continue;
				}
				visitedRoles.add(nerRole);

				tagPositions.add(tp);
				if (roleCategory.size() == 1) {
					final String startTag = "<" + roleCategory.get(0).name() + ">";
					final String endTag = "</" + roleCategory.get(0).name() + ">";
					result = result.replaceAll("\\b" + nativeRole + "\\b", startTag + nativeRole + endTag);
				} else {
					String startTag = "";
					String endTag = "";

					final int stringLength = nativeRole.length();
					if (roleCategory.size() > stringLength) {
						String replaceText = "";
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							replaceText += startTag + nativeRole + endTag;
						}
						result = result.replaceAll("\\b" + nativeRole + "\\b", replaceText);
					} else {
						String replaceText = new String(nativeRole);
						int beginIndex = 0;
						int endIndex = stringLength / roleCategory.size();
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							final String substring = nativeRole.substring(beginIndex, endIndex);
							replaceText = replaceText.replace(substring, startTag + substring + endTag);
							beginIndex = endIndex;
							endIndex = endIndex + endIndex;
							final int offset = nativeRole.length() - endIndex;
							if (offset < stringLength / roleCategory.size()) {
								endIndex += offset;
							}

						}
						result = result.replaceAll("\\b" + nativeRole + "\\b", replaceText);
					}
				}
			}
		}
		return result;
	}
	
	private static TagPosition convertPosition(TagPosition candicatePosition,  Map<Integer, NerTag> statistic) {
		final int staticOffset = candicatePosition.getTag().indexOf('<'); 
		int offset = 0;
		if(staticOffset==-1){
			for(Entry<Integer, NerTag> entry:statistic.entrySet()){
				if((entry.getKey()-offset)>candicatePosition.getStartIndex()){				
					final int start = candicatePosition.getStartIndex()+offset;
					final TagPosition result = new TagPosition(candicatePosition.getTag(),start, start+candicatePosition.getLength());
					return result; 
				}else{
					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition()-tag.getStartPosition();
					int tagLength = 2+tag.getNerTag().text.length();
					if(diff>=tagLength){
						offset += Math.abs(diff-tagLength);
					}else{
						offset -= Math.abs(diff-tagLength);
					}
				}
			}
			final int start = candicatePosition.getStartIndex()+offset;
			final TagPosition result = new TagPosition(candicatePosition.getTag(),start, start+candicatePosition.getLength());
			return result;
		}
		else{
			for(Entry<Integer, NerTag> entry:statistic.entrySet()){
				if((entry.getKey()-offset)==candicatePosition.getStartIndex()+staticOffset){				
					final int start = entry.getKey()-staticOffset;
					//final TagPosition result = new TagPosition(candicatePosition.getTag(),start, start+candicatePosition.getLength()-1);
					final TagPosition result = new TagPosition(candicatePosition.getTag(),start, start+entry.getValue().getEndPosition());
					return result; 
				}else{
					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition()-tag.getStartPosition();
					int tagLength = 2+tag.getNerTag().text.length();
					if(diff>=tagLength){
						offset += Math.abs(diff-tagLength);
					}else{
						offset -= Math.abs(diff-tagLength);
					}
				}
			}
		}

		return null;
	}
	
	private static Map<String, Set<Category>> generateNerDictionary(Map<String, Set<Category>> originalDictionary) {
		Map<String, Set<Category>> nerDictinary = new LinkedHashMap<>();

		for (Entry<String, Set<Category>> entry : originalDictionary.entrySet()) {
			final String text = entry.getKey();
			final Set<Category> categories = entry.getValue();
			try {
				final String nerTaggedResult = NERTagger.runTaggerString(text);
				final Set<Category> set = nerDictinary.get(nerTaggedResult);
				if (set == null) {
					nerDictinary.put(nerTaggedResult, new HashSet<>(categories));
				} else {
					Set<Category> newSet = new HashSet<>(set);
					newSet.addAll(categories);
					nerDictinary.put(nerTaggedResult, newSet);
				}
				//System.err.println(nerTaggedResult);
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		
		nerDictinary = MapUtil.sortByKeyDescendingNumberOfWords(nerDictinary);
		//printFullStatistic(nerDictinary);
		return nerDictinary;
	}

	private ChartConfig createChartConfiguration(final String annotatedText) {
		final PieChartConfig config = new PieChartConfig();
		final Map<String, Double> statistic = createStatistic(annotatedText);
		config.data().labels(statistic.keySet().stream().toArray(String[]::new))
				.addDataset(new PieDataset().label("Dataset 1")).and();

		config.options().responsive(true).title().display(true).text("Frequnecy Chart").and().animation()
				.animateScale(true).animateRotate(true).and().done();

		for (final Dataset<?, ?> ds : config.data().getDatasets()) {
			PieDataset lds = (PieDataset) ds;
			List<Double> data = new ArrayList<>();
			List<String> colors = new ArrayList<>();

			for (Entry<String, Double> entry : statistic.entrySet()) {
				data.add(entry.getValue());
				colors.add(ColorUtil.colorMap.get(Category.valueOf(entry.getKey())));
			}

			lds.backgroundColor(colors.toArray(new String[colors.size()]));
			lds.dataAsList(data);
		}
		return config;
	}

	private Map<String, Double> createStatistic(String annotatedText) {
		final Map<String, Double> statistic = new LinkedHashMap<>();
		final Pattern pattern = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");
		final Matcher matcher = pattern.matcher(annotatedText);
		int sumOfTags = 0;
		while (matcher.find()) {
			final String tag = matcher.group(0);
			if (!tag.contains("/")) {
				statistic.merge(tag.substring(1, tag.length() - 1), 1., Double::sum);
			}
			sumOfTags++;
		}
		sumOfTags /= 2;
		for (Entry<String, Double> entry : statistic.entrySet()) {
			statistic.put(entry.getKey(), entry.getValue() / sumOfTags);
		}
		return statistic;
	}

	private Label createColorIndicator() {
		final Label result = new Label("", ContentMode.HTML);
		final StringBuilder resultText = new StringBuilder();
		for (Entry<Category, String> entry : ColorUtil.colorMap.entrySet()) {
			resultText.append("<mark" + entry.getValue() + ">" + entry.getKey() + "</mark" + entry.getValue() + ">")
					.append("<br>");
		}
		result.setValue(resultText.toString());
		return result;

	}

	private String annotateText(String text, Map<String, Set<Category>> map) {
		String result = new String(text);
		for (final Entry<String, Set<Category>> roleEntity : map.entrySet()) {
			final String role = roleEntity.getKey().toLowerCase();
			final List<Category> roleCategory = new ArrayList<>(roleEntity.getValue());
			final Pattern pattern = Pattern.compile("(?i)" + "\\b" + role + "\\b");
			final Matcher matcher = pattern.matcher(text);
			final Set<String> visitedRoles = new HashSet<>();
			while (matcher.find()) {
				final String nativeRole = matcher.group(0);
				final TagPosition tp = new TagPosition(nativeRole,matcher.start(), matcher.end());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}
				if (visitedRoles.contains(nativeRole)) {
					continue;
				}
				visitedRoles.add(nativeRole);

				tagPositions.add(tp);
				if (roleCategory.size() == 1) {
					final String startTag = "<" + roleCategory.get(0).name() + ">";
					final String endTag = "</" + roleCategory.get(0).name() + ">";
					result = result.replaceAll("\\b" + nativeRole + "\\b", startTag + nativeRole + endTag);
				} else {
					String startTag = "";
					String endTag = "";

					final int stringLength = nativeRole.length();
					if (roleCategory.size() > stringLength) {
						String replaceText = "";
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							replaceText += startTag + nativeRole + endTag;
						}
						result = result.replaceAll("\\b" + nativeRole + "\\b", replaceText);
					} else {
						String replaceText = new String(nativeRole);
						int beginIndex = 0;
						int endIndex = stringLength / roleCategory.size();
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							final String substring = nativeRole.substring(beginIndex, endIndex);
							replaceText = replaceText.replace(substring, startTag + substring + endTag);
							beginIndex = endIndex;
							endIndex = endIndex + endIndex;
							final int offset = nativeRole.length() - endIndex;
							if (offset < stringLength / roleCategory.size()) {
								endIndex += offset;
							}

						}
						result = result.replaceAll("\\b" + nativeRole + "\\b", replaceText);
					}
				}
			}
		}
		return result;
	}

	private String addColor(String text) {
		String result = new String(text);
		for (Entry<Category, String> colorCatEnity : ColorUtil.colorMap.entrySet()) {
			result = result.replaceAll(colorCatEnity.getKey().name(), "mark" + colorCatEnity.getValue());
		}
		return result;
	}

	private Button createButton() {
		final Button run = new Button("Annotate Roles");
		return run;
	}

	private TextArea createTextArea() {
		final TextArea textArea = new TextArea();
		textArea.setImmediate(true);
		textArea.setSizeFull();
		return textArea;
	}
}
