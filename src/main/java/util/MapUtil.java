package util;

import java.util.*;

public class MapUtil {

	/**
	 * Sort a Map by value in descending order
	 ** 
	 * @param map
	 * @return a sorted map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	/**
	 * Sort a Map by key in asscending order
	 ** 
	 * @param map
	 * @return a sorted map
	 */
	public static <K, V extends Comparable<? super V>> Map<String, V> sortByKeyAscending(Map<String, V> map) {
		List<Map.Entry<String, V>> list = new LinkedList<Map.Entry<String, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, V>>() {
			public int compare(Map.Entry<String, V> o1, Map.Entry<String, V> o2) {
				return (o1.getKey().length() - o2.getKey().length());
			}
		});

		LinkedHashMap<String, V> result = new LinkedHashMap<String, V>();
		for (Map.Entry<String, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	/**
	 * Sort a Map by key in descending order
	 ** 
	 * @param map
	 * @return a sorted map
	 */
	public static <K, V extends Comparable<? super V>> Map<String, V> sortByKeyDescending(Map<String, V> map) {
		List<Map.Entry<String, V>> list = new LinkedList<Map.Entry<String, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, V>>() {
			public int compare(Map.Entry<String, V> o1, Map.Entry<String, V> o2) {
				return (o2.getKey().length() - o1.getKey().length());
			}
		});

		LinkedHashMap<String, V> result = new LinkedHashMap<String, V>();
		for (Map.Entry<String, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * Sort a Map by value in ascending order
	 ** 
	 * @param map
	 * @return a sorted map
	 */
	public static Map<Integer, Float> sortByValueAscending(final Map<Integer, Float> unsortMap) {
		// Convert Map to List
		final List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());
		// Sort list with comparator, to compare the Map values
		final Comparator<Map.Entry<Integer, Float>> comparator = new Comparator<Map.Entry<Integer, Float>>() {
			public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2) {
				if (o1.getValue().floatValue() > o2.getValue().floatValue()) {
					return 1;
				} else if (o1.getValue().floatValue() < o2.getValue().floatValue()) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(list, comparator);
		// Convert sorted map back to a Map
		Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
		for (Iterator<Map.Entry<Integer, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}