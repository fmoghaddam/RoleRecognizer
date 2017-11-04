import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import main.RoleListProvider;
import main.RoleListProviderFileBased;
import main.RoleTagger;
import model.Category;
import model.DataSourceType;
import model.NerTag;
import model.Order;
import model.TagPosition;
import model.TagPositions;
import util.CustomNERTagger;
import util.MapUtil;
import util.NERTagger;

public class Test {

	public static void main(String[] args) {
		// final RoleListProvider provider = new RoleListProviderDummy();
//		final RoleListProvider provider = new RoleListProviderFileBased();
//		provider.loadRoles(DataSourceType.WIKIPEDIA);
//		final Map<String, Set<Category>> generatedNerDictionary = generateNerDictionary(provider.getData());
		
//		System.err.println(generatedNerDictionary);
//		System.out.println(annotateText("CEO CEO CEO CEO",
//				provider.getData())); 
//		System.out.println(annotateTextWihtNER("CEO CEO CEO CEO",
//				provider.getData()));
		
		//System.err.println(CustomNERTagger.runTaggerString("Mr. Pope bought a house."));
		System.err.println(annotateTextWihtCustomeNER("King of England visited Pope Francis to play chess and use his king."));
	}

	
	private static String annotateTextWihtCustomeNER(String text) {
		StringBuilder result = new StringBuilder(text);
		final Map<Integer, NerTag> nerXmlParser = CustomNERTagger.nerXmlParser(CustomNERTagger.runTaggerXML(text));
		int offset = 0;
		for(Entry<Integer, NerTag> e:nerXmlParser.entrySet()) {
			final int start = e.getValue().getStartPosition();
			final int end = e.getValue().getEndPosition();
			
			String replace = "<"+e.getValue().getNerTag()+">" +result.substring(start+offset, end+offset) + "<"+e.getValue().getNerTag()+">" ;
			result.replace(start+offset, end+offset, replace);
			offset+=e.getValue().getNerTag().text.length()*2 + 4;
		}
		return result.toString();
	}
	
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RoleTagger.class);
	private final static TagPositions tagPositions = new TagPositions();


	@SuppressWarnings("unused")
	private static String annotateTextWihtNER(String text, Map<String, Set<Category>> map) {
		List<TagPosition> replacements = new ArrayList<>();

		String result = new String(text);

		final Map<Integer, NerTag> nerStatistic = NERTagger.nerXmlParser(NERTagger.runTaggerXML(result));
		final String resultNer = NERTagger.runTaggerString(result);

		final Map<String, Set<Category>> generatedNerDictionary = generateNerDictionary(map);

		for (final Entry<String, Set<Category>> roleEntity : generatedNerDictionary.entrySet()) {
			final List<Category> roleCategory = new ArrayList<>(roleEntity.getValue());

			final String role = roleEntity.getKey().replaceAll("\\.", "\\\\.");
			if (role.charAt(0) == '<' && role.charAt(role.length() - 1) == '>') {
				continue;
			}
			if (role.equalsIgnoreCase("the <LOCATION>") || role.equalsIgnoreCase("The <ORGANIZATION>")) {
				continue;
			}
			String regexPattern = "(?im)";
			if (role.charAt(0) != '<') {
				regexPattern += "\\b";
			}
			regexPattern += role;
			if (role.charAt(role.length() - 1) != '>') {
				regexPattern += "\\b";
			}

			final Pattern pattern = Pattern.compile("(?im)" + regexPattern);
			final Matcher matcher = pattern.matcher(resultNer);
			//final Set<String> visitedRoles = new HashSet<>();
			while (matcher.find()) {
				final String nerRole = matcher.group(0);
				TagPosition tp = new TagPosition(nerRole, matcher.start(), matcher.end());
				tp = convertPosition(tp, nerStatistic);
				final String nativeRole = result.substring(tp.getStartIndex(), tp.getEndIndex());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}
//				if (visitedRoles.contains(nerRole)) {
//					continue;
//				}
//				visitedRoles.add(nerRole);

				tagPositions.add(tp);
				if (roleCategory.size() == 1) {

					final String startTag = "<" + roleCategory.get(0).name() + ">";
					final String endTag = "</" + roleCategory.get(0).name() + ">";

					replacements
					.add(new TagPosition(startTag + nativeRole + endTag, tp.getStartIndex(), tp.getEndIndex()));

					// result = result.replaceAll("\\b" + nativeRole + "\\b",
					// startTag + nativeRole + endTag);
				} else {
					String startTag = "";
					String endTag = "";

					final int stringLength = nativeRole.length();
					if (roleCategory.size() > stringLength) {
						String replaceText = "";
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							replaceText += startTag + nativeRole + endTag;
						}
						replacements.add(new TagPosition(replaceText, tp.getStartIndex(), tp.getEndIndex()));
						// result = result.replaceAll("\\b" + nativeRole +
						// "\\b", replaceText);
					} else {
						String replaceText = new String(nativeRole);
						int beginIndex = 0;
						int endIndex = stringLength / roleCategory.size();
						int step = stringLength / roleCategory.size();
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							final String substring = nativeRole.substring(beginIndex, endIndex);
							replaceText = replaceText.replace(substring, startTag + substring + endTag);
							beginIndex = endIndex;
							endIndex = endIndex + step;
							final int offset = nativeRole.length() - endIndex;
							if (offset < stringLength / roleCategory.size()) {
								endIndex += offset;
							}

						}
						replacements.add(new TagPosition(replaceText, tp.getStartIndex(), tp.getEndIndex()));
						// result = result.replaceAll("\\b" + nativeRole +
						// "\\b", replaceText);
					}
				}
			}
		}
		replacements = sort(replacements, Order.ASC);
		int offset = 0;
		for (int i = 0; i < replacements.size(); i++) {
			final TagPosition p = replacements.get(i);
			result = result.substring(0, p.getStartIndex() + offset) + p.getTag()
			+ result.substring(p.getEndIndex() + offset);

			final Pattern pattern = Pattern.compile("<[^>]*>");
			final Matcher matcher = pattern.matcher(p.getTag());
			int diff = 0;
			while (matcher.find()) {
				diff+=matcher.group(0).length();
			}

			offset+=diff;
		}
		return result;
	}

	private static List<TagPosition> sort(List<TagPosition> replacements, Order asc) {
		TagPosition[] array = new TagPosition[replacements.size()];
		array = replacements.toArray(array);

		for (int i = 0; i < replacements.size(); i++) {
			for (int j = i + 1; j < replacements.size(); j++) {
				switch (asc) {
				case ASC:
					if (array[i].getStartIndex() > array[j].getStartIndex()) {
						TagPosition temp = array[i];
						array[i] = array[j];
						array[j] = temp;
					}
					break;
				case DESC:
					if (array[i].getStartIndex() < array[j].getStartIndex()) {
						TagPosition temp = array[i];
						array[i] = array[j];
						array[j] = temp;
					}
					break;
				}
			}
		}
		return Arrays.asList(array);
	}

	private static TagPosition convertPosition(TagPosition candicatePosition, final Map<Integer, NerTag> statistic) {
		final int staticOffset = candicatePosition.getTag().indexOf('<');
		int offset = 0;
		if (staticOffset == -1) {
			for (Entry<Integer, NerTag> entry : statistic.entrySet()) {
				if ((entry.getKey() - offset) > candicatePosition.getStartIndex()) {
					final int start = candicatePosition.getStartIndex() + offset;
					final TagPosition result = new TagPosition(candicatePosition.getTag(), start,
							start + candicatePosition.getLength());
					return result;
				} else {
					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition() - tag.getStartPosition();
					int tagLength = 2 + tag.getNerTag().text.length();
					if (diff >= tagLength) {
						offset += Math.abs(diff - tagLength);
					} else {
						offset -= Math.abs(diff - tagLength);
					}
				}
			}
			final int start = candicatePosition.getStartIndex() + offset;
			final TagPosition result = new TagPosition(candicatePosition.getTag(), start,
					start + candicatePosition.getLength());
			return result;
		} else {
			for (Entry<Integer, NerTag> entry : statistic.entrySet()) {
				if ((entry.getKey() - offset) == candicatePosition.getStartIndex() + staticOffset) {
					final int start = entry.getKey() - staticOffset;

					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition()-tag.getStartPosition();
					int tagLength = 2+tag.getNerTag().text.length();
					if(diff>=tagLength){
						offset += Math.abs(diff-tagLength);
					}else{
						offset -= Math.abs(diff-tagLength);
					}

					final TagPosition result = new TagPosition(candicatePosition.getTag(), start,
							candicatePosition.getEndIndex()+ offset);
					return result;
				} else {
					final NerTag tag = entry.getValue();
					int diff = tag.getEndPosition() - tag.getStartPosition();
					int tagLength = 2 + tag.getNerTag().text.length();
					if (diff >= tagLength) {
						offset += Math.abs(diff - tagLength);
					} else {
						offset -= Math.abs(diff - tagLength);
					}
				}
			}
		}
		return null;
	}

	private static Map<String, Set<Category>> generateNerDictionary(Map<String, Set<Category>> originalDictionary) {
		Map<String, Set<Category>> nerDictinary = new LinkedHashMap<>();

		for (Entry<String, Set<Category>> entry : originalDictionary.entrySet()) {
			final String text = entry.getKey();
			final Set<Category> categories = entry.getValue();
			try {
				final String nerTaggedResult = NERTagger.runTaggerString(text);
				final Set<Category> set = nerDictinary.get(nerTaggedResult);
				if (set == null) {
					nerDictinary.put(nerTaggedResult, new HashSet<>(categories));
				} else {
					Set<Category> newSet = new HashSet<>(set);
					newSet.addAll(categories);
					nerDictinary.put(nerTaggedResult, newSet);
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}

		nerDictinary = MapUtil.sortByKeyDescendingNumberOfWords(nerDictinary);
		return nerDictinary;
	}
	private static String annotateText(String text, Map<String, Set<Category>> map) {
		List<TagPosition> replacements = new ArrayList<>();
		String result = new String(text);
		for (final Entry<String, Set<Category>> roleEntity : map.entrySet()) {
			final List<Category> roleCategory = new ArrayList<>(roleEntity.getValue());
			final String role = roleEntity.getKey().replaceAll("\\.", "\\\\.");
			if (role.charAt(0) == '<' && role.charAt(role.length() - 1) == '>') {
				continue;
			}
			String regexPattern = "(?im)";
			regexPattern += "\\b";
			regexPattern += role;
			regexPattern += "\\b";

			final Pattern pattern = Pattern.compile("(?im)" + regexPattern);
			final Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				final String nativeRole = matcher.group(0);
				final TagPosition tp = new TagPosition(nativeRole,matcher.start(), matcher.end());
				if (tagPositions.alreadyExist(tp)) {
					continue;
				}
				tagPositions.add(tp);
				if (roleCategory.size() == 1) {
					
					final String startTag = "<" + roleCategory.get(0).name() + ">";
					final String endTag = "</" + roleCategory.get(0).name() + ">";
					replacements.add(new TagPosition(startTag+nativeRole+endTag, tp.getStartIndex(), tp.getEndIndex()));
					
//					final String startTag = "<" + roleCategory.get(0).name() + ">";
//					final String endTag = "</" + roleCategory.get(0).name() + ">";
//					result = result.replaceAll("\\b" + nativeRole + "\\b", startTag + nativeRole + endTag);
				} else {
					String startTag = "";
					String endTag = "";

					final int stringLength = nativeRole.length();
					if (roleCategory.size() > stringLength) {
						String replaceText = "";
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							replaceText += startTag + nativeRole + endTag;
						}
						replacements.add(new TagPosition( replaceText, tp.getStartIndex(), tp.getEndIndex()));
					} else {
						String replaceText = new String(nativeRole);
						int beginIndex = 0;
						int endIndex = stringLength / roleCategory.size();
						int step = stringLength / roleCategory.size();
						for (final Category cat : roleCategory) {
							startTag = "<" + cat.name() + ">";
							endTag = "</" + cat.name() + ">";
							final String substring = nativeRole.substring(beginIndex, endIndex);
							replaceText = replaceText.replace(substring, startTag + substring + endTag);
							beginIndex = endIndex;
							endIndex = endIndex + step;
							final int offset = nativeRole.length() - endIndex;
							if (offset < stringLength / roleCategory.size()) {
								endIndex += offset;
							}

						}
						replacements.add(new TagPosition(replaceText, tp.getStartIndex(), tp.getEndIndex()));
					}
				}
			}
		}
		replacements = sort(replacements, Order.ASC);
		int offset = 0;
		for (int i = 0; i < replacements.size(); i++) {
			final TagPosition p = replacements.get(i);
			result = result.substring(0, p.getStartIndex() + offset) + p.getTag()
					+ result.substring(p.getEndIndex() + offset);
			
			final Pattern pattern = Pattern.compile("<[^>]*>");
			final Matcher matcher = pattern.matcher(p.getTag());
			int diff = 0;
			while (matcher.find()) {
				diff+=matcher.group(0).length();
			}
			
			offset+=diff;
		}
		return result;
	}

}
