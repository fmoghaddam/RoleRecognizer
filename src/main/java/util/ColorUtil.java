package util;

import java.util.HashMap;
import java.util.Map;

import main.RoleListProvider.CATEGORY;

public class ColorUtil {
	public static Map<CATEGORY,String> colorMap = new HashMap<>();
	static {
		colorMap.put(CATEGORY.PRESIDENT, "yellow");
		colorMap.put(CATEGORY.KING, "red");
		colorMap.put(CATEGORY.POPE, "lightgreen");
		colorMap.put(CATEGORY.CEO, "magenta");
	}
}
