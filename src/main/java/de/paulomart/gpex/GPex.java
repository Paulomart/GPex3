package de.paulomart.gpex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Logger;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.commands.GPexCommand;
import de.paulomart.gpex.conf.GPexConfig;
import de.paulomart.gpex.conf.GPexGroupConfig;
import de.paulomart.gpex.datastorage.GPexMongoDataSorage;
import de.paulomart.gpex.datastorage.GPexMysqlDataStorage;
import de.paulomart.gpex.datastorage.PlayerDataHandler;
import de.paulomart.gpex.listeners.PlayerListener;
import de.paulomart.gpex.permissible.PermissibleInjectManager;
import de.paulomart.gpex.tag.GPexNameTagManager;
import de.paulomart.gpex.tag.NameTagEditPluginImplemention;
import de.paulomart.gpex.tag.NoNameTagChangeImplemention;
import de.paulomart.gpex.tag.ServerCoreImplemention;
import de.paulomart.gpex.utils.ClassUtils;
import de.paulomart.gpex.utils.mongo.MongoDatabaseConnector;
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
	private PlayerDataHandler gpexDataStorage;
	private MongoDatabaseConnector mongoConnector;
	private MysqlDatabaseConnector mysqlConnector;
	@Getter
	private GPexNameTagManager gpexNameTagManager;
	@Getter
	private PlayerListener playerListener;
	
	private Logger log;
	@Getter
	private boolean crashed = false;
	
	@Override
	public void onEnable(){
		instance = this;
		log = getLogger();
		
		log.info("Loading Config..");
		gpexConfig = new GPexConfig();
		gpexConfig.load();
		
		log.info("Loading GroupConfig..");
		groupConfig = new GPexGroupConfig();
		groupConfig.load();
			
		//Check what plugins we have, that we can use to make cool stuff.
		if (gpexConfig.isUseNameTagHooks()){
			log.info("Searching for NameTag hook..");
			
			if (Bukkit.getServer().getPluginManager().isPluginEnabled("NametagEdit")){
				gpexNameTagManager = new NameTagEditPluginImplemention();
				log.info("Hooked with NametagEdit");
				
			}else if (Bukkit.getServer().getPluginManager().isPluginEnabled("ServerCore")){
				gpexNameTagManager = new ServerCoreImplemention();
				log.info("Hooked with ServerCore");
				
			}else{
				gpexNameTagManager = new NoNameTagChangeImplemention();
				log.info("No NameTag hook found");
				
			}
		}else{
			gpexNameTagManager = new NoNameTagChangeImplemention();
			log.info("NameTag hooking is disabled in the config");
			
		}
		
		if (!gpexConfig.isUseMysql()){
			if (!ClassUtils.isClassLoaded("com.mongodb.Mongo")){
				stop(new ClassNotFoundException("I should use MongoDB, but the class is not loaded. Maybe you are using a custom build?"));
				return;
			}
			
			log.info("Starting mongo connection..");
			mongoConnector = new MongoDatabaseConnector(gpexConfig.getMongoHost(), gpexConfig.getMongoPort(), gpexConfig.getMongoUser(), gpexConfig.getMongoPassword(), gpexConfig.getMongoDatabase());
			
			if (mongoConnector.connect()){
				log.info("Connected to mongo.");
			}else{
				stop(new ConnectException("Could not connect to mongo"));
				return;
			}
			
			gpexDataStorage = new PlayerDataHandler(new GPexMongoDataSorage(mongoConnector, gpexConfig.getMongoCollection()));
		}else{
			log.info("Starting mysql connection..");
			mysqlConnector = new MysqlDatabaseConnector(this, gpexConfig.getMysqlHost(), gpexConfig.getMysqlPort(), gpexConfig.getMysqlUser(), gpexConfig.getMysqlPassword(), gpexConfig.getMysqlDatabase());
			
			if (mysqlConnector.connect()){
				log.info("Connected to mysql.");
			}else{
				stop(new ConnectException("Could not connect to mysql"));
				return;
			}
			
			gpexDataStorage = new PlayerDataHandler(new GPexMysqlDataStorage(mysqlConnector, gpexConfig.getMysqlTable()));	
		}
		
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
		try{
			permissionManager.onDisable();
			if (mysqlConnector != null){
				mysqlConnector.dissconnect();
			}
			if (mongoConnector != null){
				mongoConnector.dissconnect();
			}
			gpexConfig.save();
		} catch (Exception exception){
			if (!crashed){
				stop(exception);
			}else{
				log.severe("GPex crashed while disabling, but it was allready crashing so I did not log the errors");
			}
		}
	}	
	
	public String getVersion(){
		return getDescription().getVersion();
	}
	
	@Override
	public String toString(){
		return ClassUtils.classToString(this);
	}
	
	public void stop(Exception exception){
		exception.printStackTrace();
		log.severe("GPex has stopped running, see exception above.");
		crashed = true;
		try {
			String fileName = "gpex-crashreport.log";
			FileWriter fileWriter = new FileWriter(new File(fileName));
			fileWriter.write("# GPex crashreport. - Version: "+getVersion());
			fileWriter.write("\n# Its mutch, we know.\n");
			fileWriter.write("\n\n# Exception\n\n");
			fileWriter.write(exception.toString());
			fileWriter.write("\n\n# GPex-State\n\n");
			fileWriter.write(toString().replaceFirst(gpexConfig.getMysqlPassword(), "--PASSWORD-REMOVED--").replaceFirst(gpexConfig.getMongoPassword(), "--PASSWORD-REMOVED--"));
			fileWriter.write("\n# You won one free hug! Reedem at @Paulomart");
			fileWriter.flush();
			fileWriter.close();
			log.severe("The full error was pasted into \""+fileName+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		setEnabled(false);
	}
}
