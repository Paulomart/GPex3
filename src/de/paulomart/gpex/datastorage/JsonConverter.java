package de.paulomart.gpex.datastorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Getter;

import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexGroup;
import de.paulomart.gpex.permissions.GPexPermission;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.DateUtils;

@SuppressWarnings("unchecked")
public class JsonConverter {

	private GPex gpex;
	@Getter
	private ContainerFactory containerFactory;
	
	public JsonConverter(){
		gpex = GPex.getInstance();
		containerFactory = new ContainerFactory(){
			public List<Object> creatArrayContainer() {
				return new LinkedList<Object>();
		    }

			public Map<String, Object> createObjectContainer() {
				return new LinkedHashMap<String, Object>();
			}                  
		};
	}
		
	public GPexPermissionData constructPermissionData(Map<Object, Object> json, boolean exactCopy){
		GPexPermissionData playerPermissions = new GPexPermissionData();
				
		if (gpex.getGroupConfig().getGroups().get((String) json.get("group")) != null){
			if (exactCopy){
				playerPermissions = new GPexPermissionData(gpex.getGroupConfig().getGroups().get((String) json.get("group")));
			}else{
				playerPermissions.setGroup(gpex.getGroupConfig().getGroups().get((String) json.get("group")));
			}
		}
				
		if (json.get("tabprefix") != null){
			playerPermissions.setTabPrefix((String) json.get("tabprefix"));
		}

		if (json.get("tabsuffix") != null){
			playerPermissions.setTabSuffix((String) json.get("tabsuffix"));
		}
		
		if (json.get("chatprefix") != null){
			playerPermissions.setChatPrefix((String) json.get("chatprefix"));
		}
		
		if (json.get("chatsuffix") != null){
			playerPermissions.setChatSuffix((String) json.get("chatsuffix"));
		}
				
		List<String> permissions = (List<String>) json.get("permissions");
		if (permissions != null){
			for (String permission : permissions){
				if (permission != null && !permission.equalsIgnoreCase(""))
					playerPermissions.getExtraPermissions().add(new GPexPermission(permission));
			}
		}
		return playerPermissions;
	}
	
	public String constructJson(SortedMap<Long, GPexPermissionData> permissionData, GPexPermissionData basePermissionData){
		Map<Object, Object> json = new LinkedHashMap<Object, Object>();
		
		if (basePermissionData != null){
			json.put("base", constructJson(basePermissionData));
		}
		
		if  (permissionData != null && !permissionData.isEmpty()){
			for (Long time : permissionData.keySet()){
				json.put(DateUtils.stringFromDate(new Date(time)), constructJson(permissionData.get(time)));
			}
		}
		return JSONValue.toJSONString(json);
	}
	
	public Map<String, Object> constructJson(GPexPermissionData permissionData){
		Map<String, Object> json = new LinkedHashMap<String, Object>();
		
		if  (permissionData.getChatPrefix() != null){
			json.put("chatprefix", permissionData.getChatPrefix());
		}
		
		if  (permissionData.getChatSuffix() != null){
			json.put("chatsuffix", permissionData.getChatSuffix());
		}
		
		if  (permissionData.getTabPrefix() != null){
			json.put("tabprefix", permissionData.getTabPrefix());
		}
		
		if  (permissionData.getTabSuffix() != null){
			json.put("tabsuffix", permissionData.getTabSuffix());
		}
		
		if (permissionData.getGroup() != null){
			json.put("group", permissionData.getGroup().getName());
		}
		
		if (permissionData.getExtraPermissions() != null && !permissionData.getExtraPermissions().isEmpty()){
			List<String> permissions = new ArrayList<String>();
			for (GPexPermission gpexPermission : permissionData.getExtraPermissions()){
				permissions.add(gpexPermission.toString());
			}
			json.put("permissions", permissions);
		}
		
		return (json.isEmpty() ? null : json);
	}
	
	public SortResult getSortedActivePermissions(String jsonInput, boolean exactCopy){
		try {
			JSONParser parser = new JSONParser();

			Map<String, Object> json = (Map<String, Object>) parser.parse(jsonInput, containerFactory);
			SortedMap<Long, GPexPermissionData> sortedPermissionData = new TreeMap<Long, GPexPermissionData>();	
			GPexPermissionData basePlayerPermissions = null;
			
			for (String key : json.keySet()){
				Map<Object, Object> value = (Map<Object, Object>) json.get(key);			
				GPexPermissionData playerPermissions = constructPermissionData(value, exactCopy);
				
				if (key.equalsIgnoreCase("base")){
					GPexGroup group = playerPermissions.getGroup();
					if (group == null){
						group = gpex.getGroupConfig().getDefaultGroup();
					}
					if (exactCopy){
						basePlayerPermissions = mergeNotNull(new GPexPermissionData(group), playerPermissions);
					}else{
						basePlayerPermissions = new GPexPermissionData();
						basePlayerPermissions.setGroup(group);
						basePlayerPermissions = mergeNotNull(basePlayerPermissions, playerPermissions);
					}

					continue;
				}
				
				Date date = DateUtils.dateFromString(key);
				
				if (date == null){
					gpex.getLogger().warning("Could not prase date from "+ key);
				}
				
				if (System.currentTimeMillis() < date.getTime()){
					sortedPermissionData.put(date.getTime(), playerPermissions);
				}
			}
					
			return new SortResult(sortedPermissionData, basePlayerPermissions);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return new SortResult(new TreeMap<Long, GPexPermissionData>(), null);
	}
	
	public GPexPermissionData mergeNotNull(GPexPermissionData orginal, GPexPermissionData toBeAdded){
		if (toBeAdded.getChatPrefix() != null){
			orginal.setChatPrefix(toBeAdded.getChatPrefix());
		}
		
		if (toBeAdded.getChatSuffix() != null){
			orginal.setChatSuffix(toBeAdded.getChatSuffix());
		}
		
		if (toBeAdded.getTabPrefix() != null){
			orginal.setTabPrefix(toBeAdded.getTabPrefix());
		}
		
		if (toBeAdded.getTabSuffix() != null){
			orginal.setTabSuffix(toBeAdded.getTabSuffix());
		}
		
		if (toBeAdded.getGroup() != null){
			orginal.setGroup(toBeAdded.getGroup());
		}
		
		orginal.getExtraPermissions().addAll(toBeAdded.getExtraPermissions());
		
		return orginal;
	}
	
	@Getter
	public class SortResult{
		private SortedMap<Long, GPexPermissionData> sortedPermissionData;
		private GPexPermissionData basePlayerPermissions;
		
		public SortResult(SortedMap<Long, GPexPermissionData> sortedPermissionData,	GPexPermissionData basePlayerPermissions) {
			this.sortedPermissionData = sortedPermissionData;
			this.basePlayerPermissions = basePlayerPermissions;
		}
	}
	
}
