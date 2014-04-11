package de.paulomart.gpex.permissible;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexPermission;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.BukkitUtils;

public class PermissibleGPex extends PermissibleBase {

	public static enum PermissionValue {TRUE, FALSE, NOTSET};
	public static final String DOTREG = "\\.";
	public static final int CASHETIME = 60000;
	
	@Setter
	@Getter
	private Permissible previousPermissible;
	private ChildablePermission permissionRoot = new ChildablePermission();
	
	@Getter
	private Player player;
	@Getter
	private GPexPermissionData permissionData;
	private long lastPermissionDataUpdate = 0;
	private GPex gpex;
	
	//TODO: Make this shit faster.
	
	public PermissibleGPex(Player player) {
		super(player);
		gpex = GPex.getInstance();
		this.player = player;

		permissionData = new GPexPermissionData(gpex.getGroupConfig().getDefaultGroup());
		for (GPexPermission gpexPermission : permissionData.getPermissions()){
			calculateChilds(gpexPermission.getPermissionNode().toLowerCase(), permissionRoot, gpexPermission.isPositive());
		}
	}	
				
	public void calculateChilds(String permissionNode, ChildablePermission permissions, boolean positive){
		String[] permission = permissionNode.split(DOTREG);
		String keyperm = permission[0];
		
		if (keyperm.equalsIgnoreCase("*")){
			permissions.setValue(positive ? PermissionValue.TRUE : PermissionValue.FALSE);
		}else{
			ChildablePermission nextChildPermission = permissions.getChildPermissions().get(keyperm);
			if (nextChildPermission == null){
				permissions.getChildPermissions().put(keyperm, new ChildablePermission());
				nextChildPermission = permissions.getChildPermissions().get(keyperm);
			}
			
			if (permission.length != 1){
				calculateChilds(permissionNode.replaceFirst(keyperm+DOTREG, ""), nextChildPermission, positive);
			}else{
				nextChildPermission.setValue(positive ? PermissionValue.TRUE : PermissionValue.FALSE);
			}
		}
	}
	
	public void recalculatePermissions(boolean force){
		if (gpex == null){
			return;
		}
		if (lastPermissionDataUpdate + CASHETIME <= System.currentTimeMillis() || force){
			lastPermissionDataUpdate = System.currentTimeMillis();
			new Thread(new Runnable() {
				@Override
				public void run() {
					permissionData = gpex.getGpexDataStorage().getPermissionData(player.getName());
					for (GPexPermission gpexPermission : permissionData.getPermissions()){
						calculateChilds(gpexPermission.getPermissionNode(), permissionRoot, gpexPermission.isPositive());
					}
					player.setDisplayName(BukkitUtils.color(permissionData.getChatPrefix())+player.getName()+BukkitUtils.color(permissionData.getChatSuffix()));
					gpex.getGpexNameTagManager().setNameTag(player, BukkitUtils.short16(permissionData.getTabPrefix()), BukkitUtils.short16(permissionData.getTabSuffix()));
				}
			}, "GPexPermissible, recalculatingPermissions for "+player.getName()
			).start();
		}
	}
	
	@Override
	public void recalculatePermissions(){
		recalculatePermissions(false);
	}
	
	public PermissionValue getValue(String permission){
		return getValue(permission, permissionRoot, permission, PermissionValue.NOTSET);
	}
	
	private PermissionValue getValue(String permission, ChildablePermission childablePermission, String old, PermissionValue lastSetValue){
		String subPermission = permission.split(DOTREG)[0];
		
		if (childablePermission.getValue() != PermissionValue.NOTSET){
			lastSetValue = childablePermission.getValue();
		}

		if (childablePermission.hasChilds()){
			ChildablePermission subChildPermission = childablePermission.getChildPermissions().get(subPermission);
			if (subChildPermission != null){
				String str;
				
				if (permission.length() == subPermission.length()){
					str = subPermission;
				}else{
					str = old.substring(old.indexOf(subPermission)+subPermission.length()+1, old.length());
				}
				
				return getValue(str, subChildPermission, old, lastSetValue);
			}
		}
		return lastSetValue;
	}
		
	@Override
	public boolean hasPermission(String permission){
		permission = permission.toLowerCase();
		PermissionValue res = getValue(permission);
				
		if (res != PermissionValue.NOTSET){
			return res == PermissionValue.TRUE;
		}else{
			if (super.isPermissionSet(permission)){
				return super.hasPermission(permission);
			}
			
			Permission perm = Bukkit.getServer().getPluginManager().getPermission(permission);
			if (perm != null){
				return perm.getDefault().getValue(isOp());
			}else{
				return false;
			}
		}
	}
	
	@Override
	public boolean hasPermission(Permission permission){
		return hasPermission(permission.getName());
	}
	
	@Override
	public boolean isPermissionSet(String permission) {
		return getValue(permission.toLowerCase()) != PermissionValue.NOTSET;
	}
	
	@Override	
	public boolean isPermissionSet(Permission permission) {
		return isPermissionSet(permission.getName());
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
				return "this="+value.toString()+", sub="+childPermissions.toString();
			}else{
				return value.toString();
			}
		}
	}
}
