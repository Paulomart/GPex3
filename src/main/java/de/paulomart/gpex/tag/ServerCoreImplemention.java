package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

import de.paulomart.servercore.api.ServerCoreApi;
import de.paulomart.servercore.api.utils.BukkitUtils;

public class ServerCoreImplemention implements GPexNameTagManager {

	private ServerCoreApi coreApi;
	
	public ServerCoreImplemention(){
		coreApi = ServerCoreApi.getInstance();
	}
	
	public void setNameTag(Player player, String tabPrefix, String tabSuffix, String chatPrefix, String chatSuffix) {
		coreApi.getNametagAPI().setNametag(player.getName(), BukkitUtils.short16(chatPrefix), BukkitUtils.short16(chatSuffix));
		player.setDisplayName(chatPrefix + player.getDisplayName() + chatSuffix);
		player.setPlayerListName(BukkitUtils.short16(tabPrefix + player.getName()));
	}

	public void removeNameTag(Player player) {
		coreApi.getNametagAPI().resetNametag(player.getName());
		player.setDisplayName(player.getName());
		player.setPlayerListName(player.getName());
	}

}
