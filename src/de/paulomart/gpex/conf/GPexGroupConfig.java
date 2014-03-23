package de.paulomart.gpex.conf;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import lombok.Getter;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexGroup;
import de.paulomart.gpex.permissions.GPexPermission;

@Getter
public class GPexGroupConfig extends BaseConfig{

	private HashMap<String, GPexGroup> groups = new HashMap<String, GPexGroup>();
	
	public GPexGroupConfig() {
		super(GPex.getInstance(), "gpex.yml", null, false, true);
	}

	@Override
	public void onLoad() {
		System.out.println("safd");
		
		for (String groupName : config.getConfigurationSection("groups").getKeys(false)){		
			String path = "groups."+groupName;
			List<GPexPermission> permissions = new ArrayList<GPexPermission>();

			for (String rawPermission : config.getStringList(path+".permissions")){
				permissions.add(new GPexPermission(rawPermission));
			}
						
			String inherited = config.getString(path+".inherited");
			
			GPexGroup group = new GPexGroup(groupName, permissions, inherited, "chatpre", "chatsuf", "tabpre", "tabsuf");
			groups.put(groupName, group);
		}
				
		// get(Num) -> num = keyset.array[Num] -> Group
		// get(Num) = inherited Count
		
		// ## Get count of inherited Groups
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
		
		// ## Give inherited Permissions to Groups
		for(int i=0; i<sorter.size(); ++i){
			GPexGroup group = groups.get(groups.keySet().toArray()[sorter.get(i)]);
			GPexGroup inherited = groups.get(group.getInherited());			
			if (inherited != null){
				group.getPermissions().addAll(inherited.getPermissions());
			}
		}	
		
		Logger log = GPex.getInstance().getLogger();
		
		// ## Print Groups
		for(int i=0; i<groups.size(); ++i) {
			String GName = groups.keySet().toArray()[i].toString();
			log.info("----"+GName+"----");
			log.info("Permissions: "+groups.get(GName).getPermissions().toString());
			//log.info("Prio: "+groups.get(GName).getPriority());
			log.info("Prefix: "+groups.get(GName).getChatPrefix());
			log.info("Subfix: "+groups.get(GName).getChatSuffix());
		}
		log.info("Finished setting up Inherited Permissions");
	}
	
		
	@Override
	public void onSave() {
		
	}

	
	
}
