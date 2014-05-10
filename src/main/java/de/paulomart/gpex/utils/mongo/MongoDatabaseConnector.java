package de.paulomart.gpex.utils.mongo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

public class MongoDatabaseConnector {

	private String host;
	private int port;
	private String user;
	private String password;
	private String dbname;
	private WriteConcern writeConcern = WriteConcern.FSYNC_SAFE;
	
	@Getter
	private DB database;
	private Mongo mongo;
	private List<MongoDatabaseChild> databaseChilds = new ArrayList<MongoDatabaseChild>();
	
	public MongoDatabaseConnector(String host, String user, String password, String dbname){
		this(host, 27017, user, password, dbname);
	}
	
	public MongoDatabaseConnector(String host, int port, String user, String password, String dbname) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.dbname = dbname;
	}
		
	/**
	 * connects and calls {@link MongoDatabaseChild#onConnect()} if connected
	 * @return false if login incorrect or on exception
	 */
	public boolean connect(){
		try {
			mongo = new Mongo(host, port);
			mongo.setWriteConcern(writeConcern);
			database = mongo.getDB(dbname);
			if (!database.authenticate(user, password.toCharArray())){
				mongo.close();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mongo.close();
			return false;
		}
		for (MongoDatabaseChild child : databaseChilds){
			child.onConnect();
		}	
		return true;
	}
	
	/**
	 * dissconnects and calls {@link MongoDatabaseChild#onDissconnect()} if connected
	 * @return false if mongo was null, eg not connected
	 */
	public boolean dissconnect(){
		if (mongo == null){
			return false;
		}
		for (MongoDatabaseChild child : databaseChilds){
			child.onDissconnect();
		}
		mongo.close();
		return true;
	}
	
	/**
	 * Should be called from the constructor of {@link MongoDatabaseChild}<br>
	 * and calls {@link MongoDatabaseChild#onConnect()} if connected
	 * @param child
	 */
	public void addChild(MongoDatabaseChild child){
		databaseChilds.add(child);
		if (mongo != null){
			child.onConnect();
		}
	}
	
	/**
	 * removes a child and<br>
	 * calls {@link MongoDatabaseChild#onDissconnect()} if connected
	 * @param child
	 */
	public void removeChild(MongoDatabaseChild child){
		databaseChilds.remove(child);
		if (mongo != null){
			child.onDissconnect();
		}
	}
}
