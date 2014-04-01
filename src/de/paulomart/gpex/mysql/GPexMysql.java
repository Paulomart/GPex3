package de.paulomart.gpex.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.entity.Player;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import lombok.Getter;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexGroup;
import de.paulomart.gpex.permissions.GPexPermission;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseChild;
import de.paulomart.gpex.utils.mysql.MysqlDatabaseConnector;

@SuppressWarnings("unchecked")
public class GPexMysql extends MysqlDatabaseChild{

	@Getter
	private String mysqlTable;
	private ContainerFactory containerFactory;
	private GPex gpex;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	private PreparedStatement selectPlayerDataStmt;
	
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
												// 1 version - 2 ghostCount - 3 humanCount - 4 defusedCount,  5 fuseCount -  6 ghostByHumanKillCount,  7 humanByGhostKillCount, 8 gameDuratioin - 9 toString
	}
	
	@Override
	public void onDissconnect() {
		
	}
	
	public String getPlayerData(Player player){
		try {
			return unsafeGetPlayerData(player);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "{}";
	}
	
	private String unsafeGetPlayerData(Player player) throws SQLException{
		selectPlayerDataStmt.setString(1, player.getName());

		ResultSet result = selectPlayerDataStmt.executeQuery();
		if (!result.next()){
			return "{}";
		}
		return result.getString("data");
	}
	
	public String stringFromDate(Date date){
		return dateFormat.format(date);
	}
	
	public Date dateFromString(String string){
		try {
			return dateFormat.parse(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public SortResult getSortedActivePermissions(String input){
		try {
			JSONParser parser = new JSONParser();

			Map<String, Object> json = (Map<String, Object>) parser.parse(input, containerFactory);
			SortedMap<Long, GPexPermissionData> sortedPermissionData = new TreeMap<Long, GPexPermissionData>();	
			GPexPermissionData basePlayerPermissions = new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup());
			
			for (String key : json.keySet()){
				Map<Object, Object> value = (Map<Object, Object>) json.get(key);			
				GPexPermissionData playerPermissions = constructPlayerPermissions(value);
				
				if (key.equalsIgnoreCase("base")){
					GPexGroup group = playerPermissions.getGroup();
					if (group == null){
						group = gpex.getGroupConfig().getDefaultGroup();
					}
					basePlayerPermissions = mergeNotNull(new GPexPermissionData(group), playerPermissions);
					continue;
				}
				
				Date date = dateFromString(key);
				
				if (date == null){
					gpex.getLogger().warning("Could not prase date from "+ key);
				}
				
				if (System.currentTimeMillis() < date.getTime()){
					sortedPermissionData.put(date.getTime(), playerPermissions);
				}
			}
			
			if (basePlayerPermissions == null){
				basePlayerPermissions = new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup());
			}
			
			return new SortResult(sortedPermissionData, basePlayerPermissions);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return new SortResult(new TreeMap<Long, GPexPermissionData>(), new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup()));
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
	
	public GPexPermissionData constructPlayerPermissions(Map<Object, Object> json){
		GPexPermissionData playerPermissions = new GPexPermissionData();
				
		playerPermissions.setGroup(gpex.getGroupConfig().getGroups().get((String) json.get("group")));
		
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
	
	public GPexPermissionData sortPlayerPermissions(Player player){
		SortResult sortResult = getSortedActivePermissions(getPlayerData(player));
		SortedMap<Long, GPexPermissionData> sortedPermissionData = sortResult.getSortedPermissionData();
		GPexPermissionData playerPermissions = sortResult.getBasePlayerPermissions();
		
		//Overrides new 
		for (Long time : sortedPermissionData.keySet()){
			GPexPermissionData value = sortedPermissionData.get(time);
			mergeNotNull(playerPermissions, value);
		}		
			
		return playerPermissions;
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
