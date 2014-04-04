package de.paulomart.gpex;

import java.util.logging.Logger;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.commands.GPexCommand;
import de.paulomart.gpex.conf.GPexConfig;
import de.paulomart.gpex.conf.GPexGroupConfig;
import de.paulomart.gpex.datastorage.GPexDataStorage;
import de.paulomart.gpex.datastorage.GPexMysqlDataStorage;
import de.paulomart.gpex.datastorage.JsonConverter;
import de.paulomart.gpex.permissible.PermissibleInjectManager;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.tag.GPexNameTagManager;
import de.paulomart.gpex.tag.NameTagEditPluginImplemention;
import de.paulomart.gpex.tag.NoNameTagChangeImplemention;
import de.paulomart.gpex.tag.ServerCoreImplemention;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseConnector;

public class GPex extends JavaPlugin{

	@Getter
	private static GPex instance;
	
	@Getter
	private PermissibleInjectManager permissionManager;
	@Getter
	private GPexConfig gpexConfig;
	@Getter
	private GPexGroupConfig groupConfig;
	@Getter
	private GPexDataStorage gpexDataStorage;
	private MysqlDatabaseConnector mysqlConnector;
	@Getter
	private GPexNameTagManager gpexNameTagManager;
	@Getter
	private JsonConverter jsonConverter;
	
	private Logger log;
	
	@Override
	public void onEnable(){
		instance = this;
		log = getLogger();
		
		log.warning("################  W A R N I N G  ###################");
		log.warning("YOU ARE USING A UNSTABLE BUILD OF GPEX.");
		log.warning("IT MAY BREAK YOUR SERVER AND ALL THE THINGS");
		log.warning("PLESE CONSIDER DOWNLOADING A STABLE BUILD.");
		log.warning(">>>> http://dl.paul-h.de/!GPex <<<<");
		log.warning("####################################################");
		
		//Check what plugins we have, that we can use to make cool stuff.
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("NametagEdit")){
			log.info("Hooked into NametagEdit");
			gpexNameTagManager = new NameTagEditPluginImplemention();
		}else if (Bukkit.getServer().getPluginManager().isPluginEnabled("ServerCore")){
			log.info("Hooked into ServerCore");
			gpexNameTagManager = new ServerCoreImplemention();
		}else{
			log.info("No NameTag hook found");
			gpexNameTagManager = new NoNameTagChangeImplemention();
		}
		
		gpexConfig = new GPexConfig();
		gpexConfig.load();
		
		groupConfig = new GPexGroupConfig();
		groupConfig.load();
		
		jsonConverter = new JsonConverter();
		
		mysqlConnector = new MysqlDatabaseConnector(this, gpexConfig.getMysqlHost(), gpexConfig.getMysqlPort(), gpexConfig.getMysqlUser(), gpexConfig.getMysqlPassword(), gpexConfig.getMysqlDatabase());
		
		if (mysqlConnector.connect()){
			log.info("Connected to mysql.");
		}else{
			log.warning("Could not connect to mysql. exiting.");
		}
		
		gpexDataStorage = new GPexMysqlDataStorage(mysqlConnector, gpexConfig.getMysqlTable());
		
		getCommand("gpex").setExecutor(new GPexCommand());
		
		permissionManager = new PermissibleInjectManager();
	}
	
	@Override
	public void onDisable(){
		gpexConfig.save();
		
	}
	
	public GPexPermissionData getPlayerGroup(Player player){
		return gpexDataStorage.getPermissionData(player.getName());
	}
	
}
