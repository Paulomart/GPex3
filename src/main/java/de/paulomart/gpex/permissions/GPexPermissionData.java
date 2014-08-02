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
	
	public String[] formatForOutput(){
		List<String> str = new ArrayList<String>();
		if (group != null){
			str.add("§bGroup: §r"+group.getName());
		}
		if (!extraPermissions.isEmpty()){
			str.add("§bExtra permissions: ");
			for (GPexPermission permission : extraPermissions){
				str.add("§2"+permission.toString());
			}
		}
		if (chatPrefix !=null){
			str.add("§bChatPrefix: §r"+chatPrefix);
		}
		if (chatSuffix != null){
			str.add("§bChatSuffix: §r"+chatSuffix);
		}
		if (tabPrefix != null){
			str.add("§bTabPrefix: §r"+tabPrefix);
		}
		if (tabSuffix != null){
			str.add("§bTabSuffix: §r"+tabSuffix);
		}
		if (str.isEmpty()){
			str.add("§6No data");
		}
	
		String[] arr = new String[str.size()];
		arr = str.toArray(arr);
	
		return arr;
	}
}
