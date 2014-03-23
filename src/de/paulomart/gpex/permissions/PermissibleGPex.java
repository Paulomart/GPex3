package de.paulomart.gpex.permissions;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.ServerOperator;

public class PermissibleGPex extends PermissibleBase{

	@Setter
	@Getter
	private Permissible previousPermissible;
	
	public PermissibleGPex(ServerOperator opable) {
		super(opable);
		// TODO Auto-generated constructor stub
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
