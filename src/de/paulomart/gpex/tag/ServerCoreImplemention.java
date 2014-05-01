package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

import de.paulomart.servercore.api.ServerCoreApi;

public class ServerCoreImplemention implements GPexNameTagManager {

	private ServerCoreApi coreApi;
	
	public ServerCoreImplemention(){
		coreApi = ServerCoreApi.getInstance();
	}
	
	public void setNameTag(Player player, String prefix, String suffix) {
		coreApi.getNametagAPI().setNametag(player.getName(), prefix, suffix);
	}

	public void removeNameTag(Player player) {
		coreApi.getNametagAPI().resetNametag(player.getName());
	}

}
