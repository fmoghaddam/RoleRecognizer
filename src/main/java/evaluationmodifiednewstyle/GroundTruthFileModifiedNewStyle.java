package evaluationmodifiednewstyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Position;
import model.Role;

public class GroundTruthFileModifiedNewStyle {
	
	private String time;
	private String title;
	private List<Role> roles = new ArrayList<>();
	private String fullContent;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public void addRole(String role,String headRole,Position rolePhrasePosition,Map<String,String> xmlAttributes,Position rolePhraseTokenPosition){
		roles.add(new Role(role,headRole,rolePhrasePosition,xmlAttributes,rolePhraseTokenPosition));
	}
	
	public List<Role> getRoles() {
		return new ArrayList<>(roles);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFullContent() {
		return fullContent;
	}
	
	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GroundTruthFileModified2 [time=" + time + ", title=" + title + ", roles=" + roles + ", fullContent="
				+ fullContent + "]";
	}
	
}
