package main;

import java.util.HashSet;

import util.ColorUtil;

public class RoleListProviderDummy extends RoleListProvider {
	
	@Override
	public void loadRoles() {
		roleMap.put("mr. president", Category.PRESIDENT_TAG);
//		roleMap.put("President of US", Category.PRESIDENT_TAG);
//		roleMap.put("President of United States", Category.PRESIDENT_TAG);
		roleMap.put("President", Category.PRESIDENT_TAG);		
		roleMap.put("office of president", Category.PRESIDENT_TAG);
		roleMap.put("40th president of the united states", Category.PRESIDENT_TAG);
		roleMap.put("44th president of the united states", Category.PRESIDENT_TAG);
		roleMap.put("1st president of the united states", Category.PRESIDENT_TAG);
		
		
		
		

//		roleMap.put("The king", Category.KING_TAG);
//		roleMap.put("king", Category.KING_TAG);
//		roleMap.put("queen", Category.KING_TAG);
//		roleMap.put("CEO", Category.CEO_TAG);
//		roleMap.put("pope", Category.POPE_TAG);
//		roleMap.put("POTOS", Category.TOPIC_TAG);

		sortBasedOnLenghth(Order.DESC);
		ColorUtil.fill(new HashSet<>(roleMap.values()));
	}
}
