package de.paulomart.gpex.datastorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.SortedMap;

import lombok.Getter;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.datastorage.JsonConverter.SortResult;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseChild;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseConnector;

public class GPexMysqlDataStorage extends MysqlDatabaseChild implements GPexDataStorage{

	@Getter
	private String mysqlTable;
	private PreparedStatement createTableStmt;
	private PreparedStatement selectPlayerDataStmt;
	private PreparedStatement updatePlayerDataStmt;
	private GPex gpex;
	
	public GPexMysqlDataStorage(MysqlDatabaseConnector connector, String mysqlTable) {
		super(connector);
		this.mysqlTable = mysqlTable;
		gpex = GPex.getInstance();
		
		try {
			preparePrepardStatemantes();
			createTableStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void preparePrepardStatemantes() throws SQLException{
		createTableStmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `"+mysqlTable+"` ( `name` varchar(16) NOT NULL, `data` varchar(2048) DEFAULT '', UNIQUE KEY `name` (`name`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		selectPlayerDataStmt = conn.prepareStatement("select (`data`) from `"+mysqlTable+"` where `name` like ? limit 1");
		updatePlayerDataStmt = conn.prepareStatement("insert into `"+mysqlTable+"` (`data`, `name`) values(?, ?) on duplicate key update data = values(data)");
	}
	
	@Override
	public void onDissconnect() {
		try {
			selectPlayerDataStmt.close();
			updatePlayerDataStmt.close();
			createTableStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized boolean setJSONData(String player, String data){
		try {
			updatePlayerDataStmt.setString(1, data);
			updatePlayerDataStmt.setString(2, player);
			return (updatePlayerDataStmt.executeUpdate() == 1 ? true : false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized String getJSONData(String player){
		String ret = "{}";
		try {
			selectPlayerDataStmt.setString(1, player);

			ResultSet result = selectPlayerDataStmt.executeQuery();
			if (result.next()){
				ret = result.getString("data");
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
				
	public boolean addToPermissionData(String player, Date date, GPexPermissionData newPermissionData, boolean merge){
		SortResult result = gpex.getJsonConverter().getSortedActivePermissions(getJSONData(player), false);
		SortedMap<Long, GPexPermissionData> permissionData = result.getSortedPermissionData();
		if (!permissionData.containsKey(date.getTime())){
			permissionData.put(date.getTime(), newPermissionData);
		}else{
			if (merge){
				GPexPermissionData orginal = permissionData.get(date.getTime());
				permissionData.put(date.getTime(), gpex.getJsonConverter().mergeNotNull(orginal, newPermissionData));
			}else{
				permissionData.put(date.getTime(), newPermissionData);
			}			
		}
		return setJSONData(player, gpex.getJsonConverter().constructJson(permissionData, result.getBasePlayerPermissions()));
	}
	
	public boolean setBasePermissionData(String player, GPexPermissionData newPermissionData, boolean merge){
		SortResult result = gpex.getJsonConverter().getSortedActivePermissions(getJSONData(player), false);
		GPexPermissionData basePermissionData;
		if (merge){
			basePermissionData = result.getBasePlayerPermissions();
			
			if (basePermissionData == null){
				basePermissionData = new GPexPermissionData();
			}
			basePermissionData = gpex.getJsonConverter().mergeNotNull(basePermissionData, newPermissionData);
		}else{
			basePermissionData = newPermissionData;
		}
		return setJSONData(player, gpex.getJsonConverter().constructJson(result.getSortedPermissionData(), basePermissionData));
	}

	@Override
	public GPexPermissionData getPermissionData(String player) {
		SortResult sortResult = gpex.getJsonConverter().getSortedActivePermissions(getJSONData(player), true);
		SortedMap<Long, GPexPermissionData> sortedPermissionData = sortResult.getSortedPermissionData();
		GPexPermissionData playerPermissions = sortResult.getBasePlayerPermissions();
		
		if (playerPermissions == null){
			playerPermissions = new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup());
		}
		
		//Overrides new 
		for (Long time : sortedPermissionData.keySet()){
			GPexPermissionData value = sortedPermissionData.get(time);
			gpex.getJsonConverter().mergeNotNull(playerPermissions, value);
		}		
			
		return playerPermissions;
	}
}
