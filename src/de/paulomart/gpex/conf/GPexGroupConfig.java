package de.paulomart.gpex.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexGroup;
import de.paulomart.gpex.permissions.GPexPermission;
import de.paulomart.gpex.utils.ConfigUtils;
import de.paulomart.gpex.utils.HttpUtils;

@Getter
public class GPexGroupConfig{
	
	private GPex gpex;
	
	private GPexGroup defaultGroup;
	private YamlConfiguration config;
	private HashMap<String, GPexGroup> groups = new HashMap<String, GPexGroup>();
	
	private File configFile;
	
	public GPexGroupConfig() {
		gpex = GPex.getInstance();
	}

	public void load(){
		if (gpex.getGpexConfig().isWebLoaded()){
			config = YamlConfiguration.loadConfiguration(HttpUtils.requestHttp(gpex.getGpexConfig().getWebURL()));
		}else{
			configFile = new File(gpex.getGpexConfig().getLocalPath());
			config = YamlConfiguration.loadConfiguration(configFile);
			
			if (!configFile.exists()){
				ConfigUtils.loadResource(gpex, "gpex.yml", config);
				config.options().copyDefaults(true);
				ConfigUtils.hardSave(config, configFile);
			}
		}
		
		onLoad();
	}
	
	public void onLoad() {	
		for (String groupName : config.getConfigurationSection("groups").getKeys(false)){		
			String path = "groups."+groupName;
			List<GPexPermission> permissions = new ArrayList<GPexPermission>();

			for (String rawPermission : config.getStringList(path+".permissions")){
				permissions.add(new GPexPermission(rawPermission));
			}

			String inherited = config.getString(path+".inherited");
			String tabprefix = config.getString(path+".tabprefix");
			String tabsuffix = config.getString(path+".tabsuffix");
			String chatprefix = config.getString(path+".chatprefix");
			String chatsuffix = config.getString(path+".chatsuffix");

			if (inherited == null){
				inherited = "";
			}
			
			if (tabprefix == null){
				tabprefix = "";
			}
			
			if (tabsuffix == null){
				tabsuffix = "";
			}
			
			if (chatprefix == null){
				chatprefix = "";
			}
			
			if (chatsuffix == null){
				chatsuffix = "";
			}
			
			GPexGroup group = new GPexGroup(groupName, permissions, inherited, chatprefix, chatsuffix, tabprefix, tabsuffix);
			groups.put(groupName, group);
		}
				
		// get(Num) -> num = keyset.array[Num] -> Group
		// get(Num) = inherited Count
		
		// Get count of inherited Groups
		List<Integer> sorter = new ArrayList<Integer>();
		for(int i=0; i<groups.size(); ++i){
			sorter.add(0);
		}
		
		// loop while and count up the inherited count.
		for(int i=0; i<groups.size(); ++i){		
			String groupName = groups.get(groups.keySet().toArray()[i]).getName();
			int prio = 0;
			while(groups.get(groupName) != null){
				groupName = groups.get(groupName).getInherited();	
				prio++;
			}
			sorter.set(prio-1, i);
		}
		
		// Give inherited Permissions to Groups
		for(int i=0; i<sorter.size(); ++i){
			GPexGroup group = groups.get(groups.keySet().toArray()[sorter.get(i)]);
			GPexGroup inherited = groups.get(group.getInherited());			
			if (inherited != null){
				group.getPermissions().addAll(inherited.getPermissions());
			}
		}	
				
		// Print Groups
		for(String groupname : groups.keySet()) {
			gpex.getLogger().info("----"+groupname+"----");
			gpex.getLogger().info("Permissions: "+groups.get(groupname).toString());
		}
		
		defaultGroup = groups.get(config.getString("defaultgroup"));
		if (defaultGroup == null){
			//TODO ERROR HANDLING
		}
	}
		
	public void onSave() {
		
	}	
}
