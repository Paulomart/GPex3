package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

import ca.wacos.nametagedit.NametagAPI;

public class NameTagEditPluginImplemention implements GPexNameTagManager{	
	
	public void setNameTag(Player player, String prefix, String suffix) {
		NametagAPI.setNametagSoft(player.getName(), prefix, suffix);
	}

	public void removeNameTag(Player player) {
		NametagAPI.resetNametag(player.getName());
	}

}
