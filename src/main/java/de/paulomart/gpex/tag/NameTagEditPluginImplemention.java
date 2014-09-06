package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

import ca.wacos.nametagedit.NametagAPI;
import de.paulomart.servercore.api.utils.BukkitUtils;

public class NameTagEditPluginImplemention implements GPexNameTagManager{	
	
	public void setNameTag(Player player, String tabPrefix, String tabSuffix, String chatPrefix, String chatSuffix, String tagPrefix, String tagSuffix) {
		NametagAPI.setNametagSoft(player.getName(), BukkitUtils.short16(tabPrefix), BukkitUtils.short16(tabSuffix));
		player.setDisplayName(chatPrefix + player.getName() + chatSuffix);
	}

	public void removeNameTag(Player player) {
		NametagAPI.resetNametag(player.getName());
		player.setDisplayName(player.getName());
	}
	
}
