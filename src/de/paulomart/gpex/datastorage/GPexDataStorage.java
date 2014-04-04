package de.paulomart.gpex.datastorage;

import java.util.Date;

import de.paulomart.gpex.permissions.GPexPermissionData;

public interface GPexDataStorage {

	public boolean setBasePermissionData(String player, GPexPermissionData newPermissionData, boolean merage);
	
	public boolean addToPermissionData(String player, Date date, GPexPermissionData newPermissionData, boolean merage);
	
	public GPexPermissionData getPermissionData(String player);
	
	public String getJSONData(String player);
	
}
