package de.paulomart.gpex.commands;

import java.util.Date;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.paulomart.gpex.GPex;
import de.paulomart.gpex.datastorage.JsonConverter.SortResult;
import de.paulomart.gpex.permissions.GPexPermissionData;
import de.paulomart.gpex.utils.DateUtils;

public class GPexCommand implements CommandExecutor{

	private GPex gpex;
	
	public GPexCommand(){
		gpex = GPex.getInstance();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO: Should make all the commands threads.
		if (cmd.getName().equalsIgnoreCase("gpex")){
			if ((args.length == 5 || args.length == 3) && args[0].equalsIgnoreCase("set")){
				if (!sender.hasPermission("gpex.set")){
					sender.sendMessage("§cYou don't have permissions for this command");
				}
				
				String player = args[1];
				try {
					JSONParser parser = new JSONParser();
					@SuppressWarnings("unchecked")
					Map<String, Object> json = (Map<String, Object>) parser.parse(args[2], gpex.getJsonConverter().getContainerFactory());
					GPexPermissionData permissionData =	gpex.getJsonConverter().constructPermissionData(json, false);
					if (args.length == 5){
						//timelimit
						Date date = DateUtils.dateFromString(args[3]+ " " +args[4]);
						if (date == null){
							sender.sendMessage("§cCan't prase date :/");
							return false;
						}
						gpex.getGpexDataStorage().addToPermissionData(player, date, permissionData, true);
					}else{
						//edit base
						gpex.getGpexDataStorage().setBasePermissionData(player, permissionData, true);
					}
					
					sender.sendMessage("§aData updated");
					
					Player playerTarget = Bukkit.getServer().getPlayer(player);
					if (playerTarget != null){
						playerTarget.recalculatePermissions();
					}
					
					return true;
				} catch (ParseException e) {
					sender.sendMessage(e.toString());
					sender.sendMessage("§cCan't prase json :/");
				}
			}
			
			if (((args.length == 5 || args.length == 3) && args[0].equalsIgnoreCase("reset"))){
				if (!sender.hasPermission("gpex.reset")){
					sender.sendMessage("§cYou don't have permissions for this command");
				}
				
				String player = args[1];
				SortResult sortResult = gpex.getJsonConverter().getSortedActivePermissions(gpex.getGpexDataStorage().getJSONData(player), false);
				
				if (args.length == 5){
					//timelimit
					Date date = DateUtils.dateFromString(args[3]+ " " +args[4]);
					if (date == null){
						sender.sendMessage("§cCan't prase date :/");
						return false;
					}
					
					GPexPermissionData permissionData = sortResult.getSortedPermissionData().get(date.getTime());
					if (permissionData == null){
						sender.sendMessage("§cNothing to reset.");
						return false;
					}
					
					permissionData = gpex.getJsonConverter().resetData(permissionData, args[2].split(","));
					gpex.getGpexDataStorage().addToPermissionData(player, date, permissionData, false);
				}else{
					//edit base
					GPexPermissionData permissionData = sortResult.getBasePlayerPermissions();
					
					if (permissionData == null){
						sender.sendMessage("§cNothing to reset.");
						return false;
					}
					
					permissionData = gpex.getJsonConverter().resetData(permissionData, args[2].split(","));
					gpex.getGpexDataStorage().setBasePermissionData(player, permissionData, false);
				}	
				
				Player playerTarget = Bukkit.getServer().getPlayer(player);
				if (playerTarget != null){
					playerTarget.recalculatePermissions();
				}
				
				sender.sendMessage("§aData updated");
				
				return true;
			}
			
			if (args.length == 2 && args[0].equalsIgnoreCase("help")){
				if (!sender.hasPermission("gpex.help")){
					sender.sendMessage("§cYou don't have permissions for this command");
				}
				
				if (args[1].equalsIgnoreCase("commands")){
					sender.sendMessage("§b/gpex set §a<player> <data> §2[timelimit]");
					sender.sendMessage("§b/gpex reset §a<player> <dataType> §2[timelimit]");
					sender.sendMessage("§3If there is not timelimit, it will set/reset the base data.");
					return true;
				}else if (args[1].equalsIgnoreCase("parameters")){
					sender.sendMessage("§aData: §ba JSON String witch can contain this keys:");
					sender.sendMessage("§3Of type string: group, tabprefix, tabsuffix, chatprefix, chatsuffix");
					sender.sendMessage("§3Of type array: permissions");
					
					sender.sendMessage("§aTimelimit: §ba Timestamp in this format: dd-MM-yyyy HH:mm:ss");
					sender.sendMessage("§aDataType: §ba Typename of the Dataparameter, like group");
					sender.sendMessage("§3Can be a list with , between Typenames");
					sender.sendMessage("§cAll parameters don't accept spaces, exclusive the Timelimit parameter");
					
					return true;
				}
			}

			sender.sendMessage("§bGPex3 v"+gpex.getDescription().getVersion()+" by Paul Heidenreich / Paulomart, for more infos visit: http://dl.paul-h.de/!GPex3");
			sender.sendMessage("§b/gpex §ahelp commands/parameters");
		}
		
		return false;
	}

}
