package main;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import main.RoleListProvider.CATEGORY;
import util.ColorUtil;

@Theme("VaadinTest")
public class RoleTagger extends UI {

	private static final long serialVersionUID = 5924433731101343240L;
	private static Logger LOG = Logger.getLogger(RoleTagger.class);

	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		setContent(mainLayout);

		final RoleListProviderInterface provider = new RoleListProvider();
		provider.loadRoles();

		final TextArea textArea = createTExtArea();
		final Button run = createButton();
		final Label result = new Label("", ContentMode.HTML);

		run.addClickListener(event -> {
			result.setValue(annotateText(textArea.getValue(), provider.getValues()));
			LOG.info("Run button clicked");
		});

		mainLayout.addComponent(textArea);
		mainLayout.addComponent(run);
		mainLayout.addComponent(result);
		mainLayout.addComponent(createColorIndicator());
	}

	private Component createColorIndicator() {
		final Label result = new Label("", ContentMode.HTML);
		final StringBuilder resultText = new StringBuilder();
		for (Entry<CATEGORY, String> entry : ColorUtil.colorMap.entrySet()) {
			resultText.append("<mark" + entry.getValue() + ">" + entry.getKey() + "</mark" + entry.getValue() + ">")
					.append("<br>");
		}
		result.setValue(resultText.toString());
		return result;

	}

	private String annotateText(String text, Map<String, CATEGORY> map) {
		String result = new String(text);
		final String lowerCase = text.toLowerCase();
		for (Entry<String, CATEGORY> roleEntity : map.entrySet()) {
			final String role = roleEntity.getKey().toLowerCase();
			final CATEGORY roleCategory = roleEntity.getValue();
			// final int startIndex = lowerCase.indexOf(role);
			// if(startIndex>-1){
			// System.err.println(role);
			// final int endIndex = startIndex + role.length();
			// final String roleInText = text.substring(startIndex, endIndex);
			final String color = ColorUtil.colorMap.get(roleCategory) == null ? ""
					: ColorUtil.colorMap.get(roleCategory);
			// result = result.replace(roleInText,
			// "<mark"+color+">"+roleInText+"</mark"+color+">");
			// }

			Pattern pattern = Pattern.compile("(?i)" + role + "\\b");
			Matcher matcher = pattern.matcher(result);
			while (matcher.find()) {
				result = result.replaceAll(matcher.group(0) + "\\b",
						"<mark" + color + ">" + matcher.group(0) + "</mark" + color + ">");
			}

		}
		System.err.println(result);
		return result;
	}

	private Button createButton() {
		final Button run = new Button("Annotate Roles");
		return run;
	}

	private TextArea createTExtArea() {
		final TextArea textArea = new TextArea();
		textArea.setImmediate(true);
		textArea.setSizeFull();
		return textArea;
	}
}
