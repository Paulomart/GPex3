package de.paulomart.gpex.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

public class BukkitUtils {

	public static String color(String string){
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static String short16(String str){
		return shortStr(str, 16);
	}
	
	public static String shortStr(String str, int wantLength){
		if (str.length() > wantLength){
			str = str.substring(0, wantLength);
		}
		return str;
	}
	
	public static OfflinePlayer getPlayer(String name){
		return Bukkit.getOfflinePlayer(name);
	}
}
