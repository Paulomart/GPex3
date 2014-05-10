package de.paulomart.gpex.permissions;

import lombok.Getter;

@Getter
public class GPexPermission {

	private String permissionNode;
	private boolean isPositive;
	
	public GPexPermission(String rawPermission){
		isPositive = true;
		if (rawPermission.startsWith("-")){
			rawPermission = rawPermission.replaceFirst("-", "");
			isPositive = false;
		}
		permissionNode = rawPermission.toLowerCase();
	}
	
	public GPexPermission(String permissionNode, boolean isPositive) {
		this.permissionNode = permissionNode.toLowerCase();
		this.isPositive = isPositive;
	}
		
	@Override
	public String toString(){
		return (isPositive ? "" : "-")+permissionNode;
	}
}
