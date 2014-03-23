package de.paulomart.gpex;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.conf.GPexConfig;
import de.paulomart.gpex.conf.GPexGroupConfig;
import de.paulomart.gpex.permissible.PermissibleInjectManager;
import de.paulomart.gpex.permissions.GPexPlayerGroup;

public class GPex extends JavaPlugin{

	@Getter
	private static GPex instance;
	
	@Getter
	private PermissibleInjectManager permissionManager;
	@Getter
	private GPexConfig gPexConfig;
	@Getter
	private GPexGroupConfig groupConfig;
	
	
	@Override
	public void onEnable(){
		instance = this;
		
		gPexConfig = new GPexConfig();
		gPexConfig.load();
		
		groupConfig = new GPexGroupConfig();
		groupConfig.load();
		
		permissionManager = new PermissibleInjectManager();
	}
	
	@Override
	public void onDisable(){
		gPexConfig.save();
		
	}
	
	public GPexPlayerGroup getPlayerGroup(Player player){
		//TODO MYSQL STUFF.
		return new GPexPlayerGroup(groupConfig.getDefaultGroup());
	}
	
}
