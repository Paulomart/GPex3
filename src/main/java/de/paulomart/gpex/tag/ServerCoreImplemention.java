package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

import de.paulomart.gpex.utils.BukkitUtils;
import de.paulomart.servercore.api.ServerCore;
import de.paulomart.servercore.api.ServerCoreApi;

public class ServerCoreImplemention implements GPexNameTagManager {

	private ServerCoreApi coreApi;
	
	public ServerCoreImplemention(){
		coreApi = ServerCore.getApi();
	}
	
	public void setNameTag(Player player, String tabPrefix, String tabSuffix, String chatPrefix, String chatSuffix, String tagPrefix, String tagSuffix) {
		coreApi.getNametagAPI().setNametag(player.getName(), BukkitUtils.short16(tagPrefix), BukkitUtils.short16(tagSuffix));
		player.setDisplayName(chatPrefix + player.getName() + chatSuffix);
		player.setPlayerListName(BukkitUtils.short16(tabPrefix + player.getName()));
	}

	public void removeNameTag(Player player) {
		coreApi.getNametagAPI().resetNametag(player.getName());
		player.setDisplayName(player.getName());
		player.setPlayerListName(null);
	}

}
