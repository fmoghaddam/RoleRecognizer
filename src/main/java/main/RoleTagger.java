package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import util.ColorUtil;

@Theme("VaadinTest")
public class RoleTagger extends UI {

	private static final String VERIOSN = "1.2";
	private static final long serialVersionUID = 5924433731101343240L;
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RoleTagger.class);
	private final TagPostions tagPositions = new TagPostions();

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

		// final RoleListProvider provider = new RoleListProviderDummy();
		final RoleListProvider provider = new RoleListProviderFileBased();
		provider.loadRoles();

		final TextArea textArea = createTextArea();

		final ChartJs chart = new ChartJs();
		chart.setVisible(false);
		chart.setJsLoggingEnabled(true);

		final HorizontalLayout buttomLayout = new HorizontalLayout();
		buttomLayout.setSpacing(true);

		final Button annotateButton = createButton();

		final CheckBox enableTaggedText = new CheckBox("Show Annotated Text");
		enableTaggedText.setValue(false);

		final CheckBox enableAidaText = new CheckBox("Show Annotated Text For AIDA");
		enableAidaText.setValue(false);

		final CheckBox enableChart = new CheckBox("Show Frequency Chart");
		enableChart.setValue(false);

		buttomLayout.addComponent(annotateButton);
		buttomLayout.addComponent(enableChart);
		buttomLayout.addComponent(enableTaggedText);
		buttomLayout.addComponent(enableAidaText);

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

		enableAidaText.addValueChangeListener(event -> {
			annotatedAidaResult.setVisible(enableAidaText.getValue());
		});

		enableChart.addValueChangeListener(event -> {
			chart.setVisible(enableChart.getValue());
		});

		annotateButton.addClickListener(event -> {
			tagPositions.reset();
			final String annotatedText = annotateText(textArea.getValue(), provider.getValues());
			colorfullResult.setValue(addColor(annotatedText));
			annotatedResult.setValue(annotatedText);
			annotatedAidaResult.setValue(convertToAidaNotation(annotatedText));
			legend.setVisible(true);
			chart.configure(createChartConfiguration(annotatedText));
			chart.refreshData();
		});

		mainLayout.addComponent(
				new Label("<h1><Strong>Role Tagger Version " + VERIOSN + "</Strong></h1>", ContentMode.HTML));
		mainLayout.addComponent(textArea);
		mainLayout.addComponent(buttomLayout);
		mainLayout.addComponent(colorfullResult);
		mainLayout.addComponent(legend);
		mainLayout.addComponent(new Label("<Strong>Frequency Chart:</Strong>", ContentMode.HTML));
		mainLayout.addComponent(chart);
		mainLayout.addComponent(new Label("<hr />", ContentMode.HTML));
		mainLayout.addComponent(new Label("<Strong>Annotated Text:</Strong>", ContentMode.HTML));
		mainLayout.addComponent(annotatedResult);
		mainLayout.addComponent(new Label("<hr />", ContentMode.HTML));
		mainLayout.addComponent(new Label("<Strong>Annotated Text For AIDA:</Strong>", ContentMode.HTML));
		mainLayout.addComponent(annotatedAidaResult);
		mainLayout.addComponent(new Label("<hr />", ContentMode.HTML));
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

	private String convertToAidaNotation(final String text) {
		String result = new String(text);
		for (Entry<Category, String> colorCatEnity : ColorUtil.colorMap.entrySet()) {
			result = result.replaceAll("<" + colorCatEnity.getKey().name() + ">", "[[");
			result = result.replaceAll("</" + colorCatEnity.getKey().name() + ">", "]]");
		}
		return result;
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
				final TagPostion tp = new TagPostion(matcher.start(), matcher.end());
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
