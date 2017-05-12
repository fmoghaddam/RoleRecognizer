package main;

import java.util.LinkedHashMap;
import java.util.Map;

import util.MapUtil;

public abstract class RoleListProvider {
	protected Map<String, Category> roleMap = new LinkedHashMap<>();

	/**
	 * This function should first load the roles into the map, then sort the map
	 * in a descending mode regards to the length of the text and then fill the
	 * colorutil
	 */
	public void loadRoles() {
	};

	public void print() {
		for (String s : roleMap.keySet()) {
			System.err.println(s);
		}
	}

	public Map<String, Category> getValues() {
		return roleMap;
	}

	protected void sortBasedOnLenghth(Order order) {
		switch (order) {
		case ASC:
			roleMap = MapUtil.sortByKeyAscending(roleMap);
			break;
		case DESC:
			roleMap = MapUtil.sortByKeyDescending(roleMap);
			break;
		default:
			break;
		}
	}
}
