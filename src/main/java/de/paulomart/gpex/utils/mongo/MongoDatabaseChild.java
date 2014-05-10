package de.paulomart.gpex.utils.mongo;

import lombok.Getter;

import com.mongodb.DB;

public abstract class MongoDatabaseChild {

	@Getter
	private DB database;
	
	public MongoDatabaseChild(MongoDatabaseConnector connector){
		connector.addChild(this);
		database = connector.getDatabase();
	}
	
	public abstract void onDissconnect();
	
	public abstract void onConnect();
}