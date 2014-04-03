package de.paulomart.gpex.commands;

import java.util.Date;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.DateUtils;

public class GPexCommand implements CommandExecutor{

	private GPex gpex;
	
	public GPexCommand(){
		gpex = GPex.getInstance();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("gpex")){
			sender.sendMessage("/gpex set <player> <permissiondata> [timelimit]");
			sender.sendMessage("if there is no timelimit, it will set the base permissions");
			sender.sendMessage("permissiondata - can be as followed: Every tile can be removed.");
			sender.sendMessage("{\"group\":\"<group>\",\"tabprefix\":\"<string>\",\"tabsuffix\":\"<string>\",\"chatprefix\":\"<string>\",\"chatsuffix\":\"<string>\",\"permissions\":[\"permissionNode1\",\"permissionNode2\",...]}");
			sender.sendMessage("timelimit - a string be as followed: dd-MM-yyyy HH:mm:ss");
			
			if ((args.length == 5 || args.length == 3) && args[0].equalsIgnoreCase("set")){
				String player = args[1];
			
				try {
					JSONParser parser = new JSONParser();
					@SuppressWarnings("unchecked")
					Map<Object, Object> json = (Map<Object, Object>) parser.parse(args[2], gpex.getGpexMysql().getContainerFactory());
					GPexPermissionData permissionData =	gpex.getGpexMysql().constructPlayerPermissions(json, false);
					if (args.length == 5){
						//timelimit
						Date date = DateUtils.dateFromString(args[3]+ " " +args[4]);
						if (date == null){
							sender.sendMessage("can't prase date :/");
							return false;
						}
						gpex.getGpexMysql().addToPermissionData(player, date, permissionData);
					}else{
						//edit base
						gpex.getGpexMysql().setBasePermissionData(player, permissionData);
					}
					
					sender.sendMessage("Permissions set. #todo update player after setting permissions.");
					
					return true;
				} catch (ParseException e) {
					sender.sendMessage(e.toString());
					sender.sendMessage("can't prase json :/");
					return false;
				}
			}
			
		}
		
		return false;
	}

}
