package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

/**
 * Does nothing.
 * @author Paul
 *
 */
public class NoNameTagChangeImplemention implements GPexNameTagManager{

	public void setNameTag(Player player, String tabPrefix, String tabSuffix, String chatPrefix, String chatSuffix) {
		player.setDisplayName(chatPrefix + player.getName() + chatSuffix);
	}

	public void removeNameTag(Player player) {
		player.setDisplayName(player.getName());
	}

}
