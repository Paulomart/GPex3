package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

import de.paulomart.servercore.api.ServerCoreApi;

public class ServerCoreImplemention implements GPexNameTagManager {

	private ServerCoreApi coreApi;
	
	public ServerCoreImplemention(){
		coreApi = ServerCoreApi.getInstance();
	}
	
	@Override
	public void setNameTag(Player player, String prefix, String suffix) {
		coreApi.getNametagAPI().setNametag(player.getName(), prefix, suffix);
	}

	@Override
	public void removeNameTag(Player player) {
		coreApi.getNametagAPI().resetNametag(player.getName());
	}

}
