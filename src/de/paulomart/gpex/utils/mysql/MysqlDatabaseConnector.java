package de.paulomart.gpex.utils.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MysqlDatabaseConnector {

	@Getter
	private Connection conn;
	@Getter
	private List<MysqlDatabaseChild> mysqlDatabaseChilds = new ArrayList<MysqlDatabaseChild>();
	private PreparedStatement reopenConnStmt;
	private JavaPlugin plugin;
	private String mysqlUser;
	private String mysqlDatabase;
	private String mysqlPassword;
	private String mysqlHost;
	private int mysqlPort;
	
	/**
	 * Prepares the MysqlConnector, but dosent connect jet, call connect() if you are ready.
	 * @param plugin OwnerPlugin
	 */
	public MysqlDatabaseConnector(JavaPlugin plugin, String mysqlHost, int mysqlPort, String mysqlUser, String mysqlPassword, String mysqlDatabase){
		this.plugin = plugin;
		this.mysqlHost = mysqlHost;
		this.mysqlPort = mysqlPort;
		this.mysqlUser = mysqlUser;
		this.mysqlPassword = mysqlPassword;
		this.mysqlDatabase = mysqlDatabase;
	}
	
	/**
	 * Tries to connect
	 * @return true if succsess
	 */
	public boolean connect(){
		try {
			String url = "jdbc:mysql://"+mysqlHost+":"+mysqlPort+"/"+mysqlDatabase+"?autoReconnect=true";
			conn = DriverManager.getConnection(url, mysqlUser, mysqlPassword);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;	
		}
		try {
			reopenConnStmt = conn.prepareStatement("SELECT 1");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}		
		reopenconnection();
		return true;
	}
	
	/**
	 * Tries to dissconnect
	 * @return true if succsess
	 */
	public boolean dissconnect(){
		for (MysqlDatabaseChild mysqlChildDatabaseChild : mysqlDatabaseChilds){
			mysqlChildDatabaseChild.onDissconnect();
		}
		try {
			conn.close();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
    private void reopenconnection(){
    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run() {	
				try {
					reopenConnStmt.executeQuery();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				reopenconnection();
			}
						
		},36000L);
	}
}