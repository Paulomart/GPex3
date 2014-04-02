package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

public interface GPexNameTagManager {

	public void setNameTag(Player player, String prefix, String suffix);
	
	public void removeNameTag(Player player);
	
}
