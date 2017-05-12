package main;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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

	private static final long serialVersionUID = 5924433731101343240L;
	private static Logger LOG = Logger.getLogger(RoleTagger.class);
	private final TagPostions tagPositions = new TagPostions();

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setContent(mainLayout);

		//final RoleListProvider provider = new RoleListProviderDummy();
		final RoleListProvider provider = new RoleListProviderFileBased();
		provider.loadRoles();

		final TextArea textArea = createTextArea();
		
		final HorizontalLayout buttomLayout = new HorizontalLayout();
		buttomLayout.setSpacing(true);
		
		final Button annotateButton = createButton();
		final CheckBox enableTaggedText = new CheckBox("Show Annotated Text");
		enableTaggedText.setValue(false);
		
		final CheckBox enableAidaText = new CheckBox("Show Annotated Text For AIDA");
		enableAidaText.setValue(false);
		
		buttomLayout.addComponent(annotateButton);
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

		annotateButton.addClickListener(event -> {
			tagPositions.reset();
			final String annotatedText = annotateText(textArea.getValue(), provider.getValues());			
			colorfullResult.setValue(addColor(annotatedText));
			annotatedResult.setValue(annotatedText);
			annotatedAidaResult.setValue(convertToAidaNotation(annotatedText));
			legend.setVisible(true);
		});

		mainLayout.addComponent(textArea);
		mainLayout.addComponent(buttomLayout);
		mainLayout.addComponent(colorfullResult);
		mainLayout.addComponent(legend);
		mainLayout.addComponent(new Label("<hr />",ContentMode.HTML));
		mainLayout.addComponent(annotatedResult);
		mainLayout.addComponent(new Label("<hr />",ContentMode.HTML));
		mainLayout.addComponent(annotatedAidaResult);
		
	}

	private String convertToAidaNotation(final String text) {
		String result = new String(text);
		for (Entry<Category, String> colorCatEnity : ColorUtil.colorMap.entrySet()) {
			result = result.replaceAll("<"+colorCatEnity.getKey().name()+">", "[[");
			result = result.replaceAll("</"+colorCatEnity.getKey().name()+">", "]]");
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

	private String annotateText(String text, Map<String, Category> map) {
		String result = new String(text);
		for (final Entry<String, Category> roleEntity : map.entrySet()) {
			final String role = roleEntity.getKey().toLowerCase();
			final Category roleCategory = roleEntity.getValue();
			final Pattern pattern = Pattern.compile("(?i)" + "\\b"+role + "\\b");
			final Matcher matcher = pattern.matcher(text);
			final Set<String> visitedRoles = new HashSet<>(); 
			while (matcher.find()) {
				final String nativeRole = matcher.group(0);
				if(visitedRoles.contains(nativeRole)){
					continue;
				}
				visitedRoles.add(nativeRole);
				final TagPostion tp = new TagPostion(matcher.start(), matcher.end());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}
				tagPositions.add(tp);
				final String startTag = "<" + roleCategory.name() + ">";
				final String endTag = "</" + roleCategory.name() + ">";
				result = result.replaceAll("\\b" + nativeRole + "\\b",
						startTag + nativeRole + endTag);
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
