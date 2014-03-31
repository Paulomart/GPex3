package de.paulomart.gpex;

import java.util.logging.Logger;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.paulomart.gpex.conf.GPexConfig;
import de.paulomart.gpex.conf.GPexGroupConfig;
import de.paulomart.gpex.mysql.GPexMysql;
import de.paulomart.gpex.permissible.PermissibleInjectManager;
import de.paulomart.gpex.permissions.GPexPlayerGroup;
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
	private GPexMysql gpexMysql;
	private MysqlDatabaseConnector mysqlConnector;
	
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
		
		mysqlConnector = new MysqlDatabaseConnector(this, gpexConfig.getMysqlHost(), gpexConfig.getMysqlPort(), gpexConfig.getMysqlUser(), gpexConfig.getMysqlPassword(), gpexConfig.getMysqlDatabase());
		
		gpexMysql = new GPexMysql(mysqlConnector, gpexConfig.getMysqlTable());
		
		permissionManager = new PermissibleInjectManager();
	}
	
	@Override
	public void onDisable(){
		gpexConfig.save();
		
	}
	
	public GPexPlayerGroup getPlayerGroup(Player player){
		//TODO MYSQL STUFF.
		return new GPexPlayerGroup(groupConfig.getDefaultGroup());
	}
	
}
