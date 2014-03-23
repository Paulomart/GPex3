package de.paulomart.gpex;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.permissions.PermissibleInjectManager;

public class GPex extends JavaPlugin{

	@Getter
	private static GPex instance;
	
	@Getter
	private PermissibleInjectManager permissionManager;
	
	@Override
	public void onEnable(){
		instance = this;
		permissionManager = new PermissibleInjectManager();
	}
	
	@Override
	public void onDisable(){
	}
	
}
