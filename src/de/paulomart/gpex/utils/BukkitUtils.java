package de.paulomart.gpex.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BukkitUtils {

	public static String color(String string){
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static String short16(String str){
		return shortStr(str, 16);
	}
	
	public static String shortStr(String str, int wantLength){
		if (str.length() > wantLength-1){
			str = str.substring(0, wantLength-1);
		}
		return str;
	}
	
	public static Player getPlayer(String name){
		for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()){
			if (onlinePlayer.getName().equalsIgnoreCase(name)){
				return onlinePlayer;
			}
		}
		return null;
	}
}
