package evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Category;
import main.TagPostion;

public class GroundTruthProviderFileBased extends GroundTruchProvider {
	private static final String DATA_FOLDER = "groundTruth";

	@Override
	public void loadDate() {
		try {
			final File[] listOfFiles = new File(DATA_FOLDER).listFiles();
			for (int j = 0; j < listOfFiles.length; j++) {
				final String file = listOfFiles[j].getName();
				final BufferedReader br = new BufferedReader(new FileReader(DATA_FOLDER + File.separator + file));
				String text;
				while ((text = br.readLine()) != null) {
					final Pattern pattern = Pattern.compile("(?i)<.*?>");
					final Matcher matcher = pattern.matcher(text);
					TagPostion tagPositionStart = new TagPostion("",0,0);
					TagPostion tagPositionEnd = new TagPostion("",0,0);
					while (matcher.find()) {
						final String tag = matcher.group(0);
						if(!tag.contains("/")){
							tagPositionStart = new TagPostion(tag, matcher.start(), matcher.end());
						}else{
							tagPositionEnd = new TagPostion(tag, matcher.start(), matcher.end());
							final String role = text.substring(tagPositionStart.getEndIndex(), tagPositionEnd.getStartIndex());
							final String category = tagPositionStart.getTag().substring(1,tagPositionStart.getTag().length()-1).toLowerCase();
							final Set<Category> set = data.get(role);
							if(set == null){
								final Set<Category> newSet= new HashSet<>();
								newSet.add(Category.resolve(category));
								data.put(role,newSet);
							}else{
								set.add(Category.resolve(category));
								data.put(role,new HashSet<>(set));
							}
						}
					}
				}
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
