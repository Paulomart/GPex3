package de.paulomart.gpex.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.paulomart.gpex.GPex;

public class PlayerListener implements Listener{

	private GPex gpex;
	
	public PlayerListener(){
		gpex = GPex.getInstance();
		Bukkit.getServer().getPluginManager().registerEvents(this, gpex);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat(ChatColor.RESET+gpex.getGpexConfig().getChatFormat()+ChatColor.RESET);
	}
}
