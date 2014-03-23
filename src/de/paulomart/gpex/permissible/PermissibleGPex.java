package de.paulomart.gpex.permissible;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

public class PermissibleGPex extends PermissibleBase{

	@Setter
	@Getter
	private Permissible previousPermissible;
	@Getter
	private Player player;
	
	public PermissibleGPex(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public boolean hasPermission(String permission){
		System.out.println("Testing : "+permission);
		return false;
	}
	
	@Override
	public boolean hasPermission(Permission permission){
		System.out.println("Testing : "+permission.getName());
		return false;
	}
}
