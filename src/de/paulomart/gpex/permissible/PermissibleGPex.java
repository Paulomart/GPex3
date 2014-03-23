package de.paulomart.gpex.permissible;

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

	@Setter
	@Getter
	private Permissible previousPermissible;
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
	}

	@Override
	public boolean hasPermission(String permission){
		for (GPexPermission gpexPermission : personalGroup.getPermissions()){
			if (gpexPermission.getPermissionNode().equalsIgnoreCase(permission)){
				return gpexPermission.isPositive();
			}
		}
		return false;
	}
	
	@Override
	public boolean hasPermission(Permission permission){
		return hasPermission(permission.getName());
	}
}
