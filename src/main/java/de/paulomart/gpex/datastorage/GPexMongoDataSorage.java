package de.paulomart.gpex.datastorage;

import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import de.paulomart.gpex.utils.mongo.MongoDatabaseChild;
import de.paulomart.gpex.utils.mongo.MongoDatabaseConnector;

public class GPexMongoDataSorage extends MongoDatabaseChild implements GPexDataStorage{

	private DBCollection collection;
	
	public GPexMongoDataSorage(MongoDatabaseConnector connector, String collectionName) {
		super(connector);
		collection = getDatabase().getCollection(collectionName);
	}

	public String getJSONData(UUID uuid) {
		BasicDBObject row = (BasicDBObject) collection.findOne(new BasicDBObject("uuid", uuid.toString()));
		if (row == null || row.getString("gpexdata") == null){
			return "{}";
		}
		return row.getString("gpexdata");
	}

	public boolean setJSONData(UUID uuid, String jsonData) {
		collection.update(new BasicDBObject("uuid", uuid.toString()), new BasicDBObject("uuid", uuid.toString()).append("gpexdata", jsonData), true, false);
		return true;
	}

	@Override
	public void onDissconnect() {		
	}

	@Override
	public void onConnect() {		
	}

}
