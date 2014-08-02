package de.paulomart.gpex.permissions;

import org.bukkit.Bukkit;

import lombok.Getter;

@Getter
public class GPexPermission {

	private String permissionNode;
	private boolean isPositive;
	private String servername;
	
	public GPexPermission(String rawPermission){
		if (rawPermission.startsWith("s:")){
			String[] split = rawPermission.split(":");
			if (split.length == 3){
				servername = split[1];
				rawPermission = split[2];
			}
		}			
		
		isPositive = true;
		if (rawPermission.startsWith("-")){
			rawPermission = rawPermission.replaceFirst("-", "");
			isPositive = false;
		}
		permissionNode = rawPermission.toLowerCase();
	}
	
	public GPexPermission(String permissionNode, boolean isPositive, String servername) {
		this.permissionNode = permissionNode.toLowerCase();
		this.servername = servername;
		this.isPositive = isPositive;
	}
	
	public boolean isVaildOnServer(){
		if (servername == null){
			return true;
		}
		return Bukkit.getServer().getServerName().equals(servername);
	}
	
	@Override
	public String toString(){
		return (servername != null ? "s:"+servername+":" : "") + (isPositive ? "" : "-") + permissionNode;
	}
}
