package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.config.PolarAreaChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.data.PolarAreaDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.options.scale.RadialLinearScale;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import model.Category;
import model.DataSourceType;
import model.NerTag;
import model.Order;
import model.TagPosition;
import model.TagPositions;
import util.ColorUtil;
import util.CustomNERTagger;
import util.MapUtil;
import util.NERTagger;

@Theme("VaadinTest")
public class RoleTagger extends UI {

	private static final String PYTHON_CLASSIFIER_LOCATION = "/home/farshad/Python_Classifier/mainTestForJava.py";
	private static final String VERIOSN = "1.6";
	private static final long serialVersionUID = 5924433731101343240L;
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RoleTagger.class);
	private final TagPositions tagPositions = new TagPositions();

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setContent(mainLayout);

		final RoleListProvider provider = new RoleListProviderFileBased();
		provider.loadRoles(DataSourceType.WIKIPEDIA);

		final TextArea textArea = createTextArea();

		final ChartJs pieChart = new ChartJs();
		pieChart.setVisible(false);
		pieChart.setJsLoggingEnabled(true);

		final ChartJs barChart = new ChartJs();
		barChart.setVisible(false);
		barChart.setJsLoggingEnabled(true);

		final ChartJs polarChart = new ChartJs();
		polarChart.setVisible(false);
		polarChart.setJsLoggingEnabled(true);

		final HorizontalLayout buttomLayout = new HorizontalLayout();
		buttomLayout.setSpacing(true);

		final Button annotateButton = createButton();
		
		final Button generateSampleText = createSampleTextButton(textArea);

		final CheckBox enableTaggedText = new CheckBox("Show Annotated Text");
		enableTaggedText.setValue(false);

		final CheckBox enableClassifier = new CheckBox("Machine Learning Classifier");
		enableClassifier.setValue(false);

		final CheckBox enableChart = new CheckBox("Show Frequency Chart");
		enableChart.setValue(false);

		final CheckBox enableNER = new CheckBox("Use NER");
		enableNER.setValue(false);
		
		final CheckBox enableCustomeNER = new CheckBox("Use Custome NER");
		enableCustomeNER.setValue(false);

		final CheckBox selectWikipedia = new CheckBox("Use Wikipedia");
		selectWikipedia.setValue(true);

		final CheckBox selectWikidata = new CheckBox("Use Wikidata");
		selectWikidata.setValue(false);

		final Notification notif = new Notification(
				"At least one data source should be seletced",
				"",
				Notification.Type.HUMANIZED_MESSAGE);
		notif.setDelayMsec(3000);

		selectWikipedia.addValueChangeListener(event -> {
			if(!selectWikipedia.getValue()&&!selectWikidata.getValue()){
				selectWikipedia.setValue(true);
				notif.show(Page.getCurrent());
			}
		});

		selectWikidata.addValueChangeListener(event -> {
			if(!selectWikipedia.getValue()&&!selectWikidata.getValue()){
				selectWikidata.setValue(true);
				notif.show(Page.getCurrent());
			}
		});

		buttomLayout.addComponent(annotateButton);
		buttomLayout.addComponent(generateSampleText);
		buttomLayout.addComponent(enableChart);
		buttomLayout.addComponent(enableTaggedText);
		buttomLayout.addComponent(enableNER);		
		buttomLayout.addComponent(enableCustomeNER);		
		buttomLayout.addComponent(selectWikipedia);
		buttomLayout.addComponent(selectWikidata);
		buttomLayout.addComponent(enableClassifier);

		final Label annotatedResult = new Label("", ContentMode.TEXT);
		annotatedResult.setVisible(false);

		final Label annotatedAidaResult = new Label("", ContentMode.TEXT);
		annotatedAidaResult.setVisible(false);

		final Label colorfullResult = new Label("", ContentMode.HTML);
		final Label legend = createColorIndicator();
		legend.setVisible(false);
		
		enableTaggedText.addValueChangeListener(event -> {
			annotatedResult.setVisible(enableTaggedText.getValue());
		});

		enableChart.addValueChangeListener(event -> {
			pieChart.setVisible(enableChart.getValue());
			barChart.setVisible(enableChart.getValue());
			polarChart.setVisible(enableChart.getValue());
		});

		annotateButton.addClickListener(event -> {
			if(textArea.getValue().equals(null) || textArea.getValue()==""){
				final Notification notifi = new Notification(
					    "Please enter some text first",
					    "",
					    Notification.Type.HUMANIZED_MESSAGE);
				notifi.show(Page.getCurrent());
				return;
			}
			
			if(selectWikidata.getValue()&&selectWikipedia.getValue()){
				provider.loadRoles(DataSourceType.ALL);
			}else if(selectWikidata.getValue()&&!selectWikipedia.getValue()){
				provider.loadRoles(DataSourceType.WIKIDATA);
			}else if(!selectWikidata.getValue()&&selectWikipedia.getValue()){
				provider.loadRoles(DataSourceType.WIKIPEDIA);
			}
			/**
			 * Using machine learning classifier
			 * By using it, we will just get positive or negative as output
			 * So no tagging
			 */
			if(enableClassifier.getValue()) {
				try {
					String result = null;
					String printResult = "";
					Process p = Runtime.getRuntime().exec("python2 "+PYTHON_CLASSIFIER_LOCATION+" "+textArea.getValue());					
					BufferedReader stdInput = new BufferedReader(new 
							InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new 
							InputStreamReader(p.getErrorStream()));
					System.out.println("Here is the standard output of the command:\n");
					while ((result = stdInput.readLine()) != null) {
						System.out.println(result);
						printResult = result;
					}
					final Notification notification = new Notification(
							printResult,
							"",
							Notification.Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(3000);
					notification.show(Page.getCurrent());
					String s = null;
					// read any errors from the attempted command
					System.out.println("Here is the standard error of the command (if any):\n");
					while ((s = stdError.readLine()) != null) {
						System.out.println(s);
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				if(enableNER.getValue()){
					tagPositions.reset();
					final String annotatedText = annotateTextWihtNER(textArea.getValue(), provider.getData());
					colorfullResult.setValue(addColor(annotatedText));					
					annotatedResult.setValue(annotatedText);
					legend.setVisible(true);
					pieChart.configure(createPieChartConfiguration(annotatedText));
					pieChart.refreshData();
					barChart.configure(createBarChartConfiguration(annotatedText));
					barChart.refreshData();
					polarChart.configure(createPolarChartConfiguration(annotatedText));
					polarChart.refreshData();
				}	
				else if(enableCustomeNER.getValue()) {
					tagPositions.reset();
					final String annotatedText = annotateTextWihtCustomeNER(textArea.getValue());
					colorfullResult.setValue(addColor(annotatedText));					
					annotatedResult.setValue(annotatedText);
					legend.setVisible(true);
					pieChart.configure(createPieChartConfiguration(annotatedText));
					pieChart.refreshData();
					barChart.configure(createBarChartConfiguration(annotatedText));
					barChart.refreshData();
					polarChart.configure(createPolarChartConfiguration(annotatedText));
					polarChart.refreshData();
				}
				else{
					tagPositions.reset();
					final String annotatedText = annotateText(textArea.getValue(), provider.getData());
					colorfullResult.setValue(addColor(annotatedText));
					annotatedResult.setValue(annotatedText);
					legend.setVisible(true);
					pieChart.configure(createPieChartConfiguration(annotatedText));
					pieChart.refreshData();
					barChart.configure(createBarChartConfiguration(annotatedText));
					barChart.refreshData();
					polarChart.configure(createPolarChartConfiguration(annotatedText));
					polarChart.refreshData();
				}
			}
		});

		mainLayout.addComponent(
				new Label("<h1><Strong>Role Tagger Version " + VERIOSN + "</Strong></h1>", ContentMode.HTML));
		mainLayout.addComponent(textArea);
		mainLayout.addComponent(buttomLayout);
		mainLayout.addComponent(colorfullResult);
		mainLayout.addComponent(legend);
		mainLayout.addComponent(new Label("<Strong>Frequency Chart:</Strong>", ContentMode.HTML));

		HorizontalLayout chartsLayout = new HorizontalLayout();
		chartsLayout.addComponent(pieChart);
		chartsLayout.addComponent(barChart);
		chartsLayout.addComponent(polarChart);

		barChart.setHeight(300, Unit.PIXELS);

		chartsLayout.setSpacing(true);
		chartsLayout.setMargin(true);
		mainLayout.addComponent(chartsLayout);
		mainLayout.addComponent(new Label("<hr />", ContentMode.HTML));
		mainLayout.addComponent(new Label("<Strong>Annotated Text:</Strong>", ContentMode.HTML));
		mainLayout.addComponent(annotatedResult);
	}

	private String annotateTextWihtCustomeNER(String text) {
		StringBuilder result = new StringBuilder(text);
		final Map<Integer, NerTag> nerXmlParser = CustomNERTagger.nerXmlParser(CustomNERTagger.runTaggerXML(text));
		int offset = 0;
		for(Entry<Integer, NerTag> e:nerXmlParser.entrySet()) {
			final int start = e.getValue().getStartPosition();
			final int end = e.getValue().getEndPosition();
			
			String replace = "<"+e.getValue().getNerTag()+">" +result.substring(start+offset, end+offset) + "</"+e.getValue().getNerTag()+">" ;
			result.replace(start+offset, end+offset, replace);
			offset+=e.getValue().getNerTag().text.length()*2 + 5;
		}
		return result.toString();
	}

	private Button createSampleTextButton(TextArea textArea) {
		final Button run = new Button("Sample Sentense");
		final List<String> sampleSentence = Arrays.asList("The Government of Barbados (GoB), is headed by the monarch, Queen Elizabeth II as Head of State.",
				"King Edward VII oversaw a partial redecoration in a \"Belle Époque\" cream and gold colour scheme.",
				"From 1993 until 2012, the President of the Czech Republic was selected by a joint session of the parliament for a five-year term, with no more than two consecutive terms (2x Václav Havel, 2x Václav Klaus).",
				"Simón Bolívar became the first President of Colombia, and Francisco de Paula Santander was made Vice President.");
		run.addClickListener(event -> {
			int randomNum = ThreadLocalRandom.current().nextInt(0, sampleSentence.size());
			textArea.clear();
			textArea.setValue(sampleSentence.get(randomNum));
		});
		return run;
	}

	private ChartConfig createPolarChartConfiguration(String annotatedText) {
		final PolarAreaChartConfig config = new PolarAreaChartConfig();
		final Map<String, Double> statistic = createStatistic(annotatedText);
		config
		.data()
		.labels(statistic.keySet().stream().toArray(String[]::new))
		.addDataset(new PolarAreaDataset().label("My dataset").backgroundColor())
		.and();

		config.
		options()
		.responsive(true)
		.title()
		.display(true)
		.text("Polar Chart")
		.and()
		.scale(new RadialLinearScale().ticks().beginAtZero(true).and().reverse(false))
		.animation()
		.animateScale(true)
		.animateRotate(false)
		.and()
		.done();

		for (Dataset<?, ?> ds : config.data().getDatasets()) {
			final PolarAreaDataset lds = (PolarAreaDataset) ds;
			final List<Double> data = new ArrayList<>();
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

	private BarChartConfig createBarChartConfiguration(String annotatedText) {
		final BarChartConfig barConfig = new BarChartConfig();
		final Map<String, Double> statistic = createStatistic(annotatedText);		
		barConfig.
		data()
		.labels(statistic.keySet().stream().toArray(String[]::new))
		.addDataset(
				new BarDataset().backgroundColor().label("").yAxisID("y-axis-1"))                
		.and();
		barConfig.
		options()
		.responsive(true)
		.hover()
		.mode(InteractionMode.INDEX)
		.intersect(true)
		.animationDuration(400)
		.and()
		.title()
		.display(true)
		.text("Bar Chart")
		.and()
		.scales()
		.add(Axis.Y, new LinearScale().display(true).position(Position.LEFT).id("y-axis-1"))
		.and()
		.done();

		for (Dataset<?, ?> ds : barConfig.data().getDatasets()) {
			final BarDataset lds = (BarDataset) ds;
			final List<Double> data = new ArrayList<>();
			List<String> colors = new ArrayList<>();
			for (Entry<String, Double> entry : statistic.entrySet()) {
				data.add(entry.getValue());
				colors.add(ColorUtil.colorMap.get(Category.valueOf(entry.getKey())));
			}
			lds.backgroundColor(colors.toArray(new String[colors.size()]));
			lds.dataAsList(data);
		}

		return barConfig;
	}

	private String annotateTextWihtNER(String text, Map<String, Set<Category>> map) {
		List<TagPosition> replacements = new ArrayList<>();

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
			while (matcher.find()) {
				final String nerRole = matcher.group(0);
				TagPosition tp = new TagPosition(nerRole,matcher.start(), matcher.end());
				tp = convertPosition(tp,nerStatistic);
				final String nativeRole = result.substring(tp.getStartIndex(), tp.getEndIndex());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}

				tagPositions.add(tp);
				if (roleCategory.size() == 1) {
					final String startTag = "<" + roleCategory.get(0).name() + ">";
					final String endTag = "</" + roleCategory.get(0).name() + ">";
					replacements.add(new TagPosition(startTag+nativeRole+endTag, tp.getStartIndex(), tp.getEndIndex()));
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
						replacements.add(new TagPosition( replaceText, tp.getStartIndex(), tp.getEndIndex()));
					} else {
						String replaceText = new String(nativeRole);
						int beginIndex = 0;
						int endIndex = stringLength / roleCategory.size();
						int step = stringLength / roleCategory.size();
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							final String substring = nativeRole.substring(beginIndex, endIndex);
							replaceText = replaceText.replace(substring, startTag + substring + endTag);
							beginIndex = endIndex;
							endIndex = endIndex + step;
							final int offset = nativeRole.length() - endIndex;
							if (offset < stringLength / roleCategory.size()) {
								endIndex += offset;
							}

						}
						replacements.add(new TagPosition(replaceText, tp.getStartIndex(), tp.getEndIndex()));
					}
				}
			}
		}
		replacements = sort(replacements, Order.ASC);
		int offset = 0;
		for (int i = 0; i < replacements.size(); i++) {
			final TagPosition p = replacements.get(i);
			result = result.substring(0, p.getStartIndex() + offset) + p.getTag()
			+ result.substring(p.getEndIndex() + offset);

			final Pattern pattern = Pattern.compile("<[^>]*>");
			final Matcher matcher = pattern.matcher(p.getTag());
			int diff = 0;
			while (matcher.find()) {
				diff+=matcher.group(0).length();
			}

			offset+=diff;
		}
		return result;
	}

	private static List<TagPosition> sort(List<TagPosition> replacements, Order asc) {
		TagPosition[] array = new TagPosition[replacements.size()];
		array = replacements.toArray(array);

		for(int i=0;i<replacements.size();i++) {
			for(int j=i+1;j<replacements.size();j++) {
				switch (asc) {
				case ASC:
					if(array[i].getStartIndex()>array[j].getStartIndex()) {
						TagPosition temp = array[i];
						array[i] = array[j];
						array[j] = temp;
					}
					break;
				case DESC:
					if(array[i].getStartIndex()<array[j].getStartIndex()) {
						TagPosition temp = array[i];
						array[i] = array[j];
						array[j] = temp;
					}
					break;
				}
			}
		}
		return Arrays.asList(array);
	}


	private static TagPosition convertPosition(TagPosition candicatePosition, final Map<Integer, NerTag> statistic) {
		final int staticOffset = candicatePosition.getTag().indexOf('<');
		int offset = 0;
		if (staticOffset == -1) {
			for (Entry<Integer, NerTag> entry : statistic.entrySet()) {
				if ((entry.getKey() - offset) > candicatePosition.getStartIndex()) {
					final int start = candicatePosition.getStartIndex() + offset;
					final TagPosition result = new TagPosition(candicatePosition.getTag(), start,
							start + candicatePosition.getLength());
					return result;
				} else {
					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition() - tag.getStartPosition();
					int tagLength = 2 + tag.getNerTag().text.length();
					if (diff >= tagLength) {
						offset += Math.abs(diff - tagLength);
					} else {
						offset -= Math.abs(diff - tagLength);
					}
				}
			}
			final int start = candicatePosition.getStartIndex() + offset;
			final TagPosition result = new TagPosition(candicatePosition.getTag(), start,
					start + candicatePosition.getLength());
			return result;
		} else {
			for (Entry<Integer, NerTag> entry : statistic.entrySet()) {
				if ((entry.getKey() - offset) == candicatePosition.getStartIndex() + staticOffset) {
					final int start = entry.getKey() - staticOffset;

					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition()-tag.getStartPosition();
					int tagLength = 2+tag.getNerTag().text.length();
					if(diff>=tagLength){
						offset += Math.abs(diff-tagLength);
					}else{
						offset -= Math.abs(diff-tagLength);
					}

					final TagPosition result = new TagPosition(candicatePosition.getTag(), start,
							candicatePosition.getEndIndex()+ offset);
					return result;
				} else {
					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition() - tag.getStartPosition();
					int tagLength = 2 + tag.getNerTag().text.length();
					if (diff >= tagLength) {
						offset += Math.abs(diff - tagLength);
					} else {
						offset -= Math.abs(diff - tagLength);
					}
				}
			}
		}
		return null;
	}

	private Map<String, Set<Category>> generateNerDictionary(Map<String, Set<Category>> originalDictionary) {
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
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}

		nerDictinary = MapUtil.sortByKeyDescendingNumberOfWords(nerDictinary);
		return nerDictinary;
	}

	private ChartConfig createPieChartConfiguration(final String annotatedText) {
		final PieChartConfig config = new PieChartConfig();
		final Map<String, Double> statistic = createStatistic(annotatedText);
		config.data().labels(statistic.keySet().stream().toArray(String[]::new))
		.addDataset(new PieDataset().label("Dataset 1")).and();

		config.options().responsive(true).title().display(true).text("Pie Chart").and().animation()
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
		List<TagPosition> replacements = new ArrayList<>();
		String result = new String(text);
		for (final Entry<String, Set<Category>> roleEntity : map.entrySet()) {
			final List<Category> roleCategory = new ArrayList<>(roleEntity.getValue());
			final String role = roleEntity.getKey().replaceAll("\\.", "\\\\.");
			if (role.charAt(0) == '<' && role.charAt(role.length() - 1) == '>') {
				continue;
			}
			String regexPattern = "(?im)";
			regexPattern += "\\b";
			regexPattern += role;
			regexPattern += "\\b";

			final Pattern pattern = Pattern.compile("(?im)" + regexPattern);
			final Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				final String nativeRole = matcher.group(0);
				final TagPosition tp = new TagPosition(nativeRole,matcher.start(), matcher.end());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}
				tagPositions.add(tp);
				if (roleCategory.size() == 1) {

					final String startTag = "<" + roleCategory.get(0).name() + ">";
					final String endTag = "</" + roleCategory.get(0).name() + ">";
					replacements.add(new TagPosition(startTag+nativeRole+endTag, tp.getStartIndex(), tp.getEndIndex()));

					//					final String startTag = "<" + roleCategory.get(0).name() + ">";
					//					final String endTag = "</" + roleCategory.get(0).name() + ">";
					//					result = result.replaceAll("\\b" + nativeRole + "\\b", startTag + nativeRole + endTag);
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
						replacements.add(new TagPosition( replaceText, tp.getStartIndex(), tp.getEndIndex()));
					} else {
						String replaceText = new String(nativeRole);
						int beginIndex = 0;
						int endIndex = stringLength / roleCategory.size();
						int step = stringLength / roleCategory.size();
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							final String substring = nativeRole.substring(beginIndex, endIndex);
							replaceText = replaceText.replace(substring, startTag + substring + endTag);
							beginIndex = endIndex;
							endIndex = endIndex + step;
							final int offset = nativeRole.length() - endIndex;
							if (offset < stringLength / roleCategory.size()) {
								endIndex += offset;
							}

						}
						replacements.add(new TagPosition(replaceText, tp.getStartIndex(), tp.getEndIndex()));
					}
				}
			}
		}
		replacements = sort(replacements, Order.ASC);
		int offset = 0;
		for (int i = 0; i < replacements.size(); i++) {
			final TagPosition p = replacements.get(i);
			result = result.substring(0, p.getStartIndex() + offset) + p.getTag()
			+ result.substring(p.getEndIndex() + offset);

			final Pattern pattern = Pattern.compile("<[^>]*>");
			final Matcher matcher = pattern.matcher(p.getTag());
			int diff = 0;
			while (matcher.find()) {
				diff+=matcher.group(0).length();
			}

			offset+=diff;
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
