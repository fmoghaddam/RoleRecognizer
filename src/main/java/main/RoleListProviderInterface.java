package main;

import java.util.Map;

import main.RoleListProvider.CATEGORY;

public interface RoleListProviderInterface {
	void loadRoles();

	void print();
	
	Map<String,CATEGORY> getValues();
}
