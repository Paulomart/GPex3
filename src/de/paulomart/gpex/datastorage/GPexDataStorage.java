package de.paulomart.gpex.datastorage;

import java.util.Date;

import de.paulomart.gpex.permissions.GPexPermissionData;

public interface GPexDataStorage {

	public boolean setBasePermissionData(String player, GPexPermissionData newPermissionData);
	
	public boolean addToPermissionData(String player, Date date, GPexPermissionData newPermissionData);
	
	public GPexPermissionData getPermissionData(String player);
	
}
