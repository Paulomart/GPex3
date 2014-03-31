package de.paulomart.gpex.mysql;

import lombok.Getter;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseChild;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseConnector;

public class GPexMysql extends MysqlDatabaseChild{

	@Getter
	private String mysqlTable;
	
	public GPexMysql(MysqlDatabaseConnector connector, String mysqlTable) {
		super(connector);
		this.mysqlTable = mysqlTable;
	}

	@Override
	public void onDissconnect() {
		
	}

}
