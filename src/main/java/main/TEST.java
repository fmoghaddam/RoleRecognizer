package main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.vaadin.server.SystemError;

public class TEST {

	public static void main(String[] args) {
//		Map<String,Integer> m = new HashMap<>();
		
		
		
//		final RoleListProvider provider = new RoleListProviderFileBased();
//		provider.loadRoles();
//		provider.print();

//		String str1 = "Skynet";
//		String str2 = "SkyHigh asdasd asjdgsjad asjdgasjdg ajsdgajdg ajsgdajsdgajsdgjasgdjsagd jasgdjasgd jasgd ";
//		int distance = StringUtils.getLevenshteinDistance(str1, str2);
//		System.out.println("Distance :" + distance);
		
//		StringTokenizer tokenizer = new StringTokenizer("my name is farshad");
//		while(tokenizer.hasMoreTokens()) {
//		    System.out.println(tokenizer.nextToken());
//		}
		final Map<String,String> map = function();
		map.put("farshad", "rima");
		
		final Map<String,String> map2 = (Map<String, String>) ((HashMap<String, String>)map).clone();
		
		System.err.println(map.entrySet());
		//System.err.println(map2.entrySet());
		
		map2.put("farshad", "XXX");
		
		System.err.println(map.entrySet());
		//System.err.println(map2.entrySet());
		
		
		
	}
	
	private static Map<String, String> function(){
		final Map<String,String> map = new HashMap<>();
		return map;
	}
}
