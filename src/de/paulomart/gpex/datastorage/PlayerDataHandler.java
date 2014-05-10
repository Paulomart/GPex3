package de.paulomart.gpex.datastorage;

import java.util.Date;
import java.util.SortedMap;
import java.util.UUID;

import lombok.Getter;
import de.paulomart.gpex.GPex;
import de.paulomart.gpex.datastorage.JsonConverter.SortResult;
import de.paulomart.gpex.permissions.GPexPermissionData;

public class PlayerDataHandler{

	@Getter
	private GPexDataStorage storage;
	@Getter
	private JsonConverter jsonConverter;
	private GPex gpex;
	
	public PlayerDataHandler(GPexDataStorage storage){
		gpex = GPex.getInstance();
		jsonConverter = new JsonConverter();
		this.storage = storage;
	}
	
	public boolean addToPermissionData(UUID uuid, Date date, GPexPermissionData newPermissionData, boolean merge){
		SortResult result = jsonConverter.getSortedActivePermissions(storage.getJSONData(uuid), false);
		SortedMap<Long, GPexPermissionData> permissionData = result.getSortedPermissionData();
		if (!permissionData.containsKey(date.getTime())){
			permissionData.put(date.getTime(), newPermissionData);
		}else{
			if (merge){
				GPexPermissionData orginal = permissionData.get(date.getTime());
				permissionData.put(date.getTime(), jsonConverter.mergeNotNull(orginal, newPermissionData));
			}else{
				permissionData.put(date.getTime(), newPermissionData);
			}			
		}
		return storage.setJSONData(uuid, jsonConverter.constructJson(permissionData, result.getBasePlayerPermissions()));
	}
	
	public boolean setBasePermissionData(UUID uuid, GPexPermissionData newPermissionData, boolean merge){
		SortResult result = jsonConverter.getSortedActivePermissions(storage.getJSONData(uuid), false);
		GPexPermissionData basePermissionData;
		if (merge){
			basePermissionData = result.getBasePlayerPermissions();
			
			if (basePermissionData == null){
				basePermissionData = new GPexPermissionData();
			}
			basePermissionData = jsonConverter.mergeNotNull(basePermissionData, newPermissionData);
		}else{
			basePermissionData = newPermissionData;
		}
		return storage.setJSONData(uuid, jsonConverter.constructJson(result.getSortedPermissionData(), basePermissionData));
	}

	public GPexPermissionData getPermissionData(UUID uuid) {
		SortResult sortResult = jsonConverter.getSortedActivePermissions(storage.getJSONData(uuid), true);
		SortedMap<Long, GPexPermissionData> sortedPermissionData = sortResult.getSortedPermissionData();
		GPexPermissionData playerPermissions = sortResult.getBasePlayerPermissions();
		
		if (playerPermissions == null){
			playerPermissions = new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup());
		}
		
		//Overrides new 
		for (Long time : sortedPermissionData.keySet()){
			GPexPermissionData value = sortedPermissionData.get(time);
			jsonConverter.mergeNotNull(playerPermissions, value);
		}		
			
		return playerPermissions;
	}
}
