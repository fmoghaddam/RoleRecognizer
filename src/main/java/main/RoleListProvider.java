package main;

import java.util.LinkedHashMap;
import java.util.Map;

import util.MapUtil;

public class RoleListProvider implements RoleListProviderInterface {
	private Map<String,CATEGORY> roleMap = new LinkedHashMap<>();
	
	@Override
	public void loadRoles() {
		roleMap.put("President of US",CATEGORY.PRESIDENT);
		roleMap.put("President of United States",CATEGORY.PRESIDENT);
		roleMap.put("President",CATEGORY.PRESIDENT);
		roleMap.put("The king",CATEGORY.KING);
		roleMap.put("king",CATEGORY.KING);
		roleMap.put("queen",CATEGORY.KING);
		roleMap.put("CEO",CATEGORY.CEO);
		roleMap.put("pope",CATEGORY.POPE);
		
		sortBasedOnLenghth(ORDER.DESC);
	}
	
	private void sortBasedOnLenghth(ORDER order) {
//		final List<String> sortedList = new ArrayList<String>(roleMap.keySet());
		switch (order) {
		case ASC:
//			Collections.sort(sortedList, new Comparator<String>() {
//				@Override
//				public int compare(String o1, String o2) {	
//					if(o1.length()>o2.length()){
//						return 1;
//					}else{
//						return -1;
//					}
//				}
//			});
//			roleList.clear();
//			roleList.addAll(sortedList);
			
			roleMap = MapUtil.sortByKeyAscending(roleMap);
			break;
		case DESC:
			
//			Collections.sort(sortedList, new Comparator<String>() {
//				@Override
//				public int compare(String o1, String o2) {	
//					if(o1.length()>o2.length()){
//						return -1;
//					}else{
//						return 1;
//					}
//				}
//			});
//			roleList.clear();
//			roleList.addAll(sortedList);
			roleMap = MapUtil.sortByKeyDescending(roleMap);
			break;
		default:
			break;
		}
		
	}

	public enum ORDER {
		ASC,
		DESC
	}
	
	public enum CATEGORY{
		PRESIDENT,
		POPE,
		KING,
		CEO,
		TOPIC
	}

	@Override
	public void print() {
		for(String s:roleMap.keySet()){
			System.out.println(s);
		}
	}

	@Override
	public Map<String,CATEGORY> getValues() {
		return roleMap;
	}
}
