package de.paulomart.gpex.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import lombok.Getter;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexGroup;
import de.paulomart.gpex.permissions.GPexPermission;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.DateUtils;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseChild;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseConnector;

@SuppressWarnings("unchecked")
public class GPexMysql extends MysqlDatabaseChild{

	@Getter
	private String mysqlTable;
	@Getter
	private ContainerFactory containerFactory;
	private GPex gpex;

	private PreparedStatement selectPlayerDataStmt;
	private PreparedStatement updatePlayerDataStmt;
	
	public GPexMysql(MysqlDatabaseConnector connector, String mysqlTable) {
		super(connector);
		gpex = GPex.getInstance();
		this.mysqlTable = mysqlTable;
		
		try {
			preparePrepardStatemantes();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		containerFactory = new ContainerFactory(){
			public List<Object> creatArrayContainer() {
				return new LinkedList<Object>();
		    }

			public Map<Object, Object> createObjectContainer() {
				return new LinkedHashMap<Object, Object>();
			}                  
		};
	}


	public void preparePrepardStatemantes() throws SQLException{
		selectPlayerDataStmt = conn.prepareStatement("select (`data`) from `"+mysqlTable+"` where `name` like ? limit 1");
		updatePlayerDataStmt = conn.prepareStatement("insert into `"+mysqlTable+"` (`data`, `name`) values(?, ?) on duplicate key update data = values(data)");
	}
	
	@Override
	public void onDissconnect() {
		try {
			selectPlayerDataStmt.close();
			updatePlayerDataStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean setPlayerData(String player, String data){
		try {
			updatePlayerDataStmt.setString(1, data);
			updatePlayerDataStmt.setString(2, player);
			return (updatePlayerDataStmt.executeUpdate() == 1 ? true : false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getPlayerData(String player){
		try {
			selectPlayerDataStmt.setString(1, player);

			ResultSet result = selectPlayerDataStmt.executeQuery();
			if (result.next()){
				return result.getString("data");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "{}";
	}
		
	public String constructJSON(SortedMap<Long, GPexPermissionData> permissionData, GPexPermissionData basePermissionData){
		Map<Object, Object> json = new LinkedHashMap<Object, Object>();
		
		if (basePermissionData != null){
			json.put("base", jsonMapFrom(basePermissionData));
		}
		
		if  (permissionData != null && !permissionData.isEmpty()){
			for (Long time : permissionData.keySet()){
				json.put(DateUtils.stringFromDate(new Date(time)), jsonMapFrom(permissionData.get(time)));
			}
		}
		return JSONValue.toJSONString(json);
	}
		
	public void addToPermissionData(String player, Date date, GPexPermissionData newPermissionData){
		SortResult result = getSortedActivePermissions(getPlayerData("Paulomart"), false);
		SortedMap<Long, GPexPermissionData> permissionData = result.getSortedPermissionData();
		if (!permissionData.containsKey(date.getTime())){
			permissionData.put(date.getTime(), newPermissionData);
		}else{
			GPexPermissionData orginal = permissionData.get(date.getTime());
			permissionData.put(date.getTime(), mergeNotNull(orginal, newPermissionData));
		}
		
		setPlayerData(player, constructJSON(permissionData, result.getBasePlayerPermissions()));
	}
	
	public void setBasePermissionData(String player, GPexPermissionData newPermissionData){
		SortResult result = getSortedActivePermissions(getPlayerData("Paulomart"), false);
		GPexPermissionData basePermissionData = result.getBasePlayerPermissions();
		
		if (basePermissionData == null){
			basePermissionData = new GPexPermissionData();
		}
		
		basePermissionData = mergeNotNull(basePermissionData, newPermissionData);
		setPlayerData(player, constructJSON(result.getSortedPermissionData(), basePermissionData));
	}
		
	public SortResult getSortedActivePermissions(String input, boolean exactCopy){
		try {
			JSONParser parser = new JSONParser();

			Map<String, Object> json = (Map<String, Object>) parser.parse(input, containerFactory);
			SortedMap<Long, GPexPermissionData> sortedPermissionData = new TreeMap<Long, GPexPermissionData>();	
			GPexPermissionData basePlayerPermissions = null;
			
			for (String key : json.keySet()){
				Map<Object, Object> value = (Map<Object, Object>) json.get(key);			
				GPexPermissionData playerPermissions = constructPlayerPermissions(value, exactCopy);
				
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
	
	@Getter
	public class SortResult{
		private SortedMap<Long, GPexPermissionData> sortedPermissionData;
		private GPexPermissionData basePlayerPermissions;
		
		public SortResult(SortedMap<Long, GPexPermissionData> sortedPermissionData,	GPexPermissionData basePlayerPermissions) {
			this.sortedPermissionData = sortedPermissionData;
			this.basePlayerPermissions = basePlayerPermissions;
		}
	}
	
	public GPexPermissionData constructPlayerPermissions(Map<Object, Object> json, boolean exactCopy){
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
	
	public GPexPermissionData sortPlayerPermissions(String player){
		SortResult sortResult = getSortedActivePermissions(getPlayerData(player), true);
		SortedMap<Long, GPexPermissionData> sortedPermissionData = sortResult.getSortedPermissionData();
		GPexPermissionData playerPermissions = sortResult.getBasePlayerPermissions();
		
		if (playerPermissions == null){
			playerPermissions = new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup());
		}
		
		//Overrides new 
		for (Long time : sortedPermissionData.keySet()){
			GPexPermissionData value = sortedPermissionData.get(time);
			mergeNotNull(playerPermissions, value);
		}		
			
		return playerPermissions;
	}
	
	public Map<Object, Object> jsonMapFrom(GPexPermissionData permissionData){
		Map<Object, Object> json = new LinkedHashMap<Object, Object>();
		
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
}
