package de.paulomart.gpex.datastorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import lombok.Getter;

import de.paulomart.gpex.utils.mysql.MysqlDatabaseChild;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseConnector;

public class GPexMysqlDataStorage extends MysqlDatabaseChild implements GPexDataStorage{

	@Getter
	private String mysqlTable;
	private PreparedStatement createTableStmt;
	private PreparedStatement selectPlayerDataStmt;
	private PreparedStatement updatePlayerDataStmt;
	
	public GPexMysqlDataStorage(MysqlDatabaseConnector connector, String mysqlTable) {
		super(connector);
		this.mysqlTable = mysqlTable;
		
		try {
			preparePrepardStatemantes();
			createTableStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void preparePrepardStatemantes() throws SQLException{
		createTableStmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `"+mysqlTable+"` ( `name` varchar(36) NOT NULL, `data` varchar(2048) DEFAULT '', UNIQUE KEY `name` (`name`)) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
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
	
	public synchronized boolean setJSONData(UUID uuid, String data){
		try {
			updatePlayerDataStmt.setString(1, data);
			updatePlayerDataStmt.setString(2, uuid.toString());
			return (updatePlayerDataStmt.executeUpdate() == 1 ? true : false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized String getJSONData(UUID uuid){
		String ret = "{}";
		try {
			selectPlayerDataStmt.setString(1, uuid.toString());

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
}
