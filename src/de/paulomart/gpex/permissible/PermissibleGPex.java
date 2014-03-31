package de.paulomart.gpex.permissible;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexPermission;
import de.paulomart.gpex.permissions.GPexPlayerGroup;

public class PermissibleGPex extends PermissibleBase{

	public static enum PermissionValue {TRUE, FALSE, NOTSET};
	public static final String DOTREG = "";
	
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
			
		for (GPexPermission gpexPermission : personalGroup.getPermissions()){
				calc(gpexPermission.getPermissionNode(), permissions, gpexPermission.isPositive());
		}
	}	
		
	public void calc(String permissionNode, HashMap<String, ChildablePermission> permissions, boolean positive){
		String[] permission = permissionNode.split(DOTREG);
		String keyperm = permission[0];
		
		ChildablePermission childablePermission = permissions.get(keyperm);
		if (childablePermission == null){
			permissions.put(keyperm, new ChildablePermission());
			childablePermission = permissions.get(keyperm);
		}
							
		if (permission.length != 1){
			calc(permissionNode.replaceFirst(keyperm+DOTREG, ""), childablePermission.getChildPermissions(), positive);
		}else{
			childablePermission.setValue(positive ? PermissionValue.TRUE : PermissionValue.FALSE);
		}
	}
	
	@Override
	public void recalculatePermissions(){

	}

	private PermissionValue getValue(String permission, HashMap<String, ChildablePermission> permissions, String old){
		String subPermission = permission.split(DOTREG)[0];
		ChildablePermission childablePermission = permissions.get(subPermission);
		if (childablePermission == null){
			return PermissionValue.NOTSET;
		}
				
		if (childablePermission.hasChilds()){			
			String str = old.substring(old.indexOf(subPermission)+subPermission.length()+1, old.length());
			return getValue(str, childablePermission.getChildPermissions(), old);
		}
		return childablePermission.getValue();		
	}
	
	@Override
	public boolean hasPermission(String permission){
		long start = System.currentTimeMillis();
		boolean ret = getValue(permission, permissions, permission) == PermissionValue.TRUE;
		System.out.println("Permission Check took: "+ (System.currentTimeMillis() - start)+ " checked: "+permission);
		return ret;
	}
	
	@Override
	public boolean hasPermission(Permission permission){
		return hasPermission(permission.getName());
	}
	
	@Getter
	public class ChildablePermission{
		
		private HashMap<String, ChildablePermission> childPermissions = new HashMap<String, PermissibleGPex.ChildablePermission>();
		@Setter
		private PermissionValue value = PermissionValue.NOTSET;
		
		/**
		 * Use for late init.
		 */
		public ChildablePermission(){
		}
		
		public ChildablePermission(HashMap<String, ChildablePermission> childPermissions){
			this.childPermissions.putAll(childPermissions);
		}
		
		public ChildablePermission(PermissionValue value){
			this.value = value;
		}
				
		public boolean hasChilds(){
			return !childPermissions.isEmpty();
		}
		
		@Override
		public String toString(){
			if (hasChilds()){
				return childPermissions.toString();
			}else{
				return value.toString();
			}
		}
	}
}
