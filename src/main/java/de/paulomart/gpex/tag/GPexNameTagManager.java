package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

public interface GPexNameTagManager {

	public void setNameTag(Player player, String tabPrefix, String tabSuffix, String chatPrefix, String chatSuffix);
	
	public void removeNameTag(Player player);
	
}
