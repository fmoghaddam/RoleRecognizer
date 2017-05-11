package main;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class RoleListProvider {
	protected Map<String, Category> roleMap = new LinkedHashMap<>();
	
	public void loadRoles(){};

	public void print() {
		for (String s : roleMap.keySet()) {
			System.out.println(s);
		}
	}

	public Map<String, Category> getValues() {
		return roleMap;
	}
}
