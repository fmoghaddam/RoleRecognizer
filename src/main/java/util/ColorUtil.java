package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import main.Category;

public class ColorUtil {
	public static Map<Category, String> colorMap = new LinkedHashMap<>();

	private static final String[] colors = new String[] { "lightgreen", "magenta", "yellow", "red", "lightblue", "cyan",
			"orange", "pink", "gray", "green" };

	public static void fill(Collection<Category> categories) {
		final List<Category> list = new ArrayList<>(categories);
		Collections.sort(list);
		int i = 0;
		for (final Category cat : list) {
			colorMap.put(cat, colors[i++]);
		}
	}
}
