package de.paulomart.gpex.permissions;

import java.util.List;

import lombok.Data;

@Data
public class GPexGroup implements Cloneable{
	
	private List<GPexPermission> permissions;	
	private String chatPrefix;
	private String chatSuffix;
	private String tabPrefix;
	private String tabSuffix;
	private String name;
	private String inherited;
	
	public GPexGroup(String name, List<GPexPermission> permissions, String inherited, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix) {
		this.name = name;
		this.permissions = permissions;
		this.inherited = inherited;
		this.chatPrefix = chatPrefix;
		this.chatSuffix = chatSuffix;
		this.tabPrefix = tabPrefix;
		this.tabSuffix = tabSuffix;
	}
	
	public GPexGroup clone(){
		return clone();
	}

}
