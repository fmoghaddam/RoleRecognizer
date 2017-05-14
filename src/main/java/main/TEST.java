package main;

import org.apache.commons.lang.StringUtils;

public class TEST {

	public static void main(String[] args) {
		final RoleListProvider provider = new RoleListProviderFileBased();
		provider.loadRoles();
		provider.print();

//		String str1 = "Skynet";
//		String str2 = "SkyHigh asdasd asjdgsjad asjdgasjdg ajsdgajdg ajsgdajsdgajsdgjasgdjsagd jasgdjasgd jasgd ";
//		int distance = StringUtils.getLevenshteinDistance(str1, str2);
//		System.out.println("Distance :" + distance);
	}

}
