package model;

import java.util.HashMap;
import java.util.Map;

public class Role {

	private String rolePhrase;
	private String headRole;
	private Position rolePhasePosition;
	private Map<String, String> xmlAttributes;
	
	public Role(String rolePhrase, String headRole, int startRolePhrasePosition, int endRolePhrasePosition,
			Map<String, String> xmlAttributes) {
		if(rolePhrase == null){
			throw new IllegalArgumentException("RolePhrase is null");
		}
		if(headRole == null){
			throw new IllegalArgumentException("HeadRole is null for role: "+rolePhrase);
		}
		if(xmlAttributes == null || xmlAttributes.isEmpty()){
			throw new IllegalArgumentException("xmlAttributes is null or empty for role: "+rolePhrase);
		}
		this.rolePhrase = rolePhrase;
		this.headRole = headRole;
		rolePhasePosition = new Position(startRolePhrasePosition,endRolePhrasePosition);
		this.xmlAttributes = new HashMap<>(xmlAttributes);
	}


	public String getRolePhrase() {
		return rolePhrase;
	}


	public String getHeadRole() {
		return headRole;
	}


	public Position getRolePhasePosition() {
		return rolePhasePosition;
	}


	public void setRolePhasePosition(Position rolePhasePosition) {
		this.rolePhasePosition = rolePhasePosition;
	}


	public Map<String, String> getXmlAttributes() {
		return xmlAttributes;
	}

	@Override
	public String toString() {
		return "Role [rolePhrase=" + rolePhrase + ", headRole=" + headRole + ", rolePhasePosition=" + rolePhasePosition
				+ ", xmlAttributes=" + xmlAttributes + "]";
	}
	
}
