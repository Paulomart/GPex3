package de.paulomart.gpex.permissions;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GPexPlayerGroup{

	private List<GPexPermission> permissions;
	private String chatPrefix;
	private String chatSuffix;
	private String tabPrefix;
	private String tabSuffix;
	
	public GPexPlayerGroup(GPexGroup gpexGroup){
		permissions = new ArrayList<GPexPermission>(gpexGroup.getPermissions());
		chatPrefix = gpexGroup.getChatPrefix();
		chatSuffix = gpexGroup.getChatSuffix();
		tabPrefix = gpexGroup.getTabPrefix();
		tabSuffix = gpexGroup.getTabSuffix();
	}
	
}
