package de.paulomart.gpex.permissible;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexPlayerGroup;

public class PermissibleGPex extends PermissibleBase{

	public static enum PermissionValue {TRUE, FALSE, NOTSET};
	
	@Setter
	@Getter
	private Permissible previousPermissible;
	
	private HashMap<String, ChildablePermission> permissions = new HashMap<String, ChildablePermission>();
	
	@Getter
	private Player player;
	@Getter
	private GPexPlayerGroup personalGroup;
	private GPex gpex;
	
	public PermissibleGPex(Player player) {
		super(player);
		gpex = GPex.getInstance();
		this.player = player;
		personalGroup = gpex.getPlayerGroup(player);
		
		//TODO Load permissions form group... somehow..
		
		HashMap<String, ChildablePermission> permissions2 = new HashMap<String, ChildablePermission>();
		
		permissions2.put("b", new ChildablePermission(PermissionValue.TRUE));
		permissions2.put("a", new ChildablePermission(PermissionValue.TRUE));
		
		
		permissions.put("vipextras", new ChildablePermission(permissions2));
	}

	@Override
	public void recalculatePermissions(){
		
	}

	private PermissionValue getValue(String permission, HashMap<String, ChildablePermission> permissions, String old){
		String subPermission = permission.split("\\.")[0];
		ChildablePermission childablePermission = permissions.get(subPermission);
		if (childablePermission == null){
			return PermissionValue.NOTSET;
		}
				
		if (childablePermission.hasChilds()){
			return getValue(old.replaceAll(subPermission+".", ""), childablePermission.getChildPermissions(), old);
		}
		return childablePermission.getValue();		
	}
	
	@Override
	public boolean hasPermission(String permission){
		long start = System.currentTimeMillis();
		boolean ret = getValue(permission, permissions, permission) == PermissionValue.TRUE;
		System.out.println("Permission Check took: "+ (System.currentTimeMillis() - start));
		return ret;
	}
	
	@Override
	public boolean hasPermission(Permission permission){
		return hasPermission(permission.getName());
	}
	
	@Getter
	public class ChildablePermission{
		
		private HashMap<String, ChildablePermission> childPermissions = new HashMap<String, PermissibleGPex.ChildablePermission>();
		private PermissionValue value = PermissionValue.NOTSET;
		
		public ChildablePermission(HashMap<String, ChildablePermission> childPermissions){
			this.childPermissions.putAll(childPermissions);
		}
		
		public ChildablePermission(PermissionValue value){
			this.value = value;
		}
		
		public boolean hasChilds(){
			return !childPermissions.isEmpty();
		}
	}
}
