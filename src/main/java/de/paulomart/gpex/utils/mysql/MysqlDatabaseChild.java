package de.paulomart.gpex.utils.mysql;

import java.sql.Connection;

public abstract class MysqlDatabaseChild {

	protected Connection conn;
	
	public MysqlDatabaseChild(MysqlDatabaseConnector connector){
		connector.getMysqlDatabaseChilds().add(this);
		conn = connector.getConn();
	}	
	
	public abstract void onDissconnect();
	
}
