package de.paulomart.gpex;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.conf.GPexConfig;
import de.paulomart.gpex.permissible.PermissibleInjectManager;

public class GPex extends JavaPlugin{

	@Getter
	private static GPex instance;
	
	@Getter
	private PermissibleInjectManager permissionManager;
	
	@Getter
	private GPexConfig gPexConfig;
	
	@Override
	public void onEnable(){
		instance = this;
		
		gPexConfig = new GPexConfig();
		gPexConfig.load();
		
		permissionManager = new PermissibleInjectManager();
	}
	
	@Override
	public void onDisable(){
		gPexConfig.save();
		
	}
	
}
