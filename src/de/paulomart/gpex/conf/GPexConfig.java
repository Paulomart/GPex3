package de.paulomart.gpex.conf;

import lombok.Getter;

import de.paulomart.gpex.GPex;

@Getter
public class GPexConfig extends BaseConfig{

	private GPex gpex;
	
	private String mysqlHost;
	private int mysqlPort;
	private String mysqlUser;
	private String mysqlPassword;
	private String mysqlDatabase;
	private String mysqlTable;
	private boolean useNameTagHooks = true;
	
	public GPexConfig(){
		super(GPex.getInstance(), "config.yml", "config.yml", true, true);
		gpex = GPex.getInstance();
	}

	@Override
	public void onLoad() {
		mysqlDatabase = config.getString("mysql.database");
		mysqlHost = config.getString("mysql.host");
		mysqlPassword = config.getString("mysql.password");
		mysqlPort = config.getInt("mysql.port");
		mysqlUser = config.getString("mysql.user");
		mysqlTable = config.getString("mysql.table");
		useNameTagHooks = config.getBoolean("useNameTagHooks");
	}

	@Override
	public void onSave() {
		
	}

	
	
}
