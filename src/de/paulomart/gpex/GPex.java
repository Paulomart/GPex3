package de.paulomart.gpex;

import java.util.logging.Logger;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.commands.GPexCommand;
import de.paulomart.gpex.conf.GPexConfig;
import de.paulomart.gpex.conf.GPexGroupConfig;
import de.paulomart.gpex.datastorage.GPexDataStorage;
import de.paulomart.gpex.datastorage.GPexMysqlDataStorage;
import de.paulomart.gpex.datastorage.JsonConverter;
import de.paulomart.gpex.listeners.PlayerListener;
import de.paulomart.gpex.permissible.PermissibleInjectManager;
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
	@Getter
	private PlayerListener playerListener;
	
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
		
		gpexConfig = new GPexConfig();
		gpexConfig.load();
		
		groupConfig = new GPexGroupConfig();
		groupConfig.load();
		
		//Check what plugins we have, that we can use to make cool stuff.
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("NametagEdit") && gpexConfig.isUseNameTagHooks()){
			log.info("Hooked with NametagEdit");
			gpexNameTagManager = new NameTagEditPluginImplemention();
		}else if (Bukkit.getServer().getPluginManager().isPluginEnabled("ServerCore") && gpexConfig.isUseNameTagHooks()){
			log.info("Hooked with ServerCore");
			gpexNameTagManager = new ServerCoreImplemention();
		}else{
			log.info("No NameTag hook found");
			gpexNameTagManager = new NoNameTagChangeImplemention();
		}
		
		jsonConverter = new JsonConverter();
		
		mysqlConnector = new MysqlDatabaseConnector(this, gpexConfig.getMysqlHost(), gpexConfig.getMysqlPort(), gpexConfig.getMysqlUser(), gpexConfig.getMysqlPassword(), gpexConfig.getMysqlDatabase());
		
		if (mysqlConnector.connect()){
			log.info("Connected to mysql.");
		}else{
			log.warning("Could not connect to mysql. exiting.");
		}
		
		gpexDataStorage = new GPexMysqlDataStorage(mysqlConnector, gpexConfig.getMysqlTable());
		
		getCommand("gpex").setExecutor(new GPexCommand());
		
		playerListener = new PlayerListener();
		permissionManager = new PermissibleInjectManager();
		
		log.info("  _____ _____          ____  ");
		log.info(" / ____|  __ \\        |___ \\ ");
		log.info("| |  __| |__) |____  __ __) |");
		log.info("| | |_ |  ___/ _ \\ \\/ /|__ < ");
		log.info("| |__| | |  |  __/>  < ___) |");
		log.info(" \\_____|_|   \\___/_/\\_\\____/ ");
		log.info("");
	}
	
	@Override
	public void onDisable(){
		permissionManager.onDisable();
		mysqlConnector.dissconnect();
		gpexConfig.save();
		
	}	
}
