package de.paulomart.gpex.datastorage;

import java.util.Date;
import java.util.UUID;

import de.paulomart.gpex.permissions.GPexPermissionData;

public interface GPexDataStorage {

	public boolean setBasePermissionData(UUID uuid, GPexPermissionData newPermissionData, boolean merage);
	
	public boolean addToPermissionData(UUID uuid, Date date, GPexPermissionData newPermissionData, boolean merage);
	
	public GPexPermissionData getPermissionData(UUID uuid);
	
	public String getJSONData(UUID uuid);
	
}
