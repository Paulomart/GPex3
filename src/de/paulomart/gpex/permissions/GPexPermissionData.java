package de.paulomart.gpex.permissions;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GPexPermissionData {
	
	private List<GPexPermission> extraPermissions = new ArrayList<GPexPermission>();
	private GPexGroup group;
	private String chatPrefix;
	private String chatSuffix;
	private String tabPrefix;
	private String tabSuffix;
	
	public GPexPermissionData(){
		
	}
	
	public GPexPermissionData(GPexGroup gpexGroup){
		group = gpexGroup;
		chatPrefix = gpexGroup.getChatPrefix();
		chatSuffix = gpexGroup.getChatSuffix();
		tabPrefix = gpexGroup.getTabPrefix();
		tabSuffix = gpexGroup.getTabSuffix();
	}
	
	public List<GPexPermission> getPermissions(){
		List<GPexPermission> permissions = new ArrayList<GPexPermission>();
		if (group != null)
			permissions.addAll(group.getPermissions());
		permissions.addAll(extraPermissions);
		return permissions;
	}
}
