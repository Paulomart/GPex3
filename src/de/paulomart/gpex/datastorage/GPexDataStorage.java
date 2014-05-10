package de.paulomart.gpex.datastorage;

import java.util.UUID;

public interface GPexDataStorage {
	
	public String getJSONData(UUID uuid);
	
	public boolean setJSONData(UUID uuid, String jsonData);
	
}
