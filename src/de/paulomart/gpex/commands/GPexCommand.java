package de.paulomart.gpex.commands;

import java.util.Calendar;
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
			if (args.length >= 3 && args[0].equalsIgnoreCase("set")){
				if (!sender.hasPermission("gpex.set")){
					sender.sendMessage("�cYou don't have permissions for this command");
					return true;
				}
				
				String player = args[1];
				String parseJson = "";
				String parseDate = null;					
				int lastJson = args.length-1;
				
				if (!args[lastJson].endsWith("}")){
					//prase with out date.
					lastJson = lastJson-2;
				}
				
				for (int i = 2; i < lastJson+1; i++){
					parseJson += " "+args[i];
				}
				
				if (lastJson != args.length-1){
					parseDate = args[lastJson+1]+" "+args[lastJson+2];
				}
				
				try {
					JSONParser parser = new JSONParser();				
					@SuppressWarnings("unchecked")
					Map<String, Object> json = (Map<String, Object>) parser.parse(parseJson, gpex.getJsonConverter().getContainerFactory());
					GPexPermissionData permissionData =	gpex.getJsonConverter().constructPermissionData(json, false);
					if (parseDate != null){
						//timelimit
						String[] parse = parseDate.split(" ");
						Date date;
						
						if (parse[1].equalsIgnoreCase("days")){
							int days;
							try {
								days = Integer.valueOf(parse[0]);
							} catch (NumberFormatException e){
								sender.sendMessage(e.toString());
								sender.sendMessage("�cCan't prase day count: \""+parse[0]+"\"");
								return false;
							}
							date = afterDays(days);
						}else{
							date = DateUtils.dateFromString(parseDate);
							if (date == null){
								sender.sendMessage("�cCan't prase date: \""+parseDate+"\"");
								return false;
							}
						}
						
						gpex.getGpexDataStorage().addToPermissionData(player, date, permissionData, true);
					}else{
						//edit base
						gpex.getGpexDataStorage().setBasePermissionData(player, permissionData, true);
					}
					
					Player playerTarget = Bukkit.getServer().getPlayer(player);
					if (playerTarget != null){
						gpex.getPermissionManager().getPermissible(playerTarget).recalculatePermissions(true);
					}
					
					sender.sendMessage("�aData updated for "+player);
					return true;
				} catch (ParseException e) {
					sender.sendMessage(e.toString());
					sender.sendMessage("�cCan't prase json: \""+parseJson+"\"");
					return true;
				}
			}
			
			if (((args.length == 5 || args.length == 3) && args[0].equalsIgnoreCase("reset"))){
				if (!sender.hasPermission("gpex.reset")){
					sender.sendMessage("�cYou don't have permissions for this command");
					return true;
				}
				
				String player = args[1];
				SortResult sortResult = gpex.getJsonConverter().getSortedActivePermissions(gpex.getGpexDataStorage().getJSONData(player), false);
				
				if (args.length == 5){
					//timelimit
					Date date = DateUtils.dateFromString(args[3]+ " " +args[4]);
					if (date == null){
						sender.sendMessage("�cCan't prase date: \""+args[3]+ " " +args[4]+"\"");
						return false;
					}
					
					GPexPermissionData permissionData = sortResult.getSortedPermissionData().get(date.getTime());
					if (permissionData == null){
						sender.sendMessage("�cNothing to reset");
						return false;
					}
					
					permissionData = gpex.getJsonConverter().resetData(permissionData, args[2].split(","));
					gpex.getGpexDataStorage().addToPermissionData(player, date, permissionData, false);
				}else{
					//edit base
					GPexPermissionData permissionData = sortResult.getBasePlayerPermissions();
					
					if (permissionData == null){
						sender.sendMessage("�cNothing to reset");
						return false;
					}
					
					permissionData = gpex.getJsonConverter().resetData(permissionData, args[2].split(","));
					gpex.getGpexDataStorage().setBasePermissionData(player, permissionData, false);
				}	
				
				Player playerTarget = Bukkit.getServer().getPlayer(player);
				if (playerTarget != null){
					gpex.getPermissionManager().getPermissible(playerTarget).recalculatePermissions(true);
				}
				
				sender.sendMessage("�aData updated for "+player);
				return true;
			}
			
			if (args.length == 2 && args[0].equalsIgnoreCase("help")){
				if (!sender.hasPermission("gpex.help")){
					sender.sendMessage("�cYou don't have permissions for this command");
					return true;
				}
				
				if (args[1].equalsIgnoreCase("commands")){
					sender.sendMessage("�b/gpex set �a<player> <data> �2[timelimit]");
					sender.sendMessage("�b/gpex reset �a<player> <dataType> �2[timelimit]");
					sender.sendMessage("�3If there is not timelimit, it will set/reset the base data.");
					
					return true;
				}else if (args[1].equalsIgnoreCase("parameters")){
					sender.sendMessage("�aData: �ba JSON string witch can contain this keys:");
					sender.sendMessage("�3Of type string: group, tabprefix, tabsuffix, chatprefix, chatsuffix");
					sender.sendMessage("�3Of type array: permissions");
					sender.sendMessage("�aTimelimit: �ba timestamp in this format: dd-MM-yyyy HH:mm:ss");
					sender.sendMessage("�3Can be also relative in days in this format: <days> days");
					sender.sendMessage("�aDataType: �ba typename of the dataparameter, like group");
					sender.sendMessage("�3Can be a list with , between typenames");
					sender.sendMessage("�cDataType does not accept spaces!");
					
					return true;
				}
			}

			if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))){
				if (args.length == 0){
					sender.sendMessage("�b�lGPex3 �bv"+gpex.getVersion()+" �3by Paul Heidenreich / Paulomart, for more infos visit: http://dl.paul-h.de/!GPex3");
				}
				sender.sendMessage("�b/gpex �ahelp commands/parameters");
			}else{
				sender.sendMessage("�cUnknown command. Type \"/gpex\" for help");
			}
			return true;
		}
		return false;
	}
	
	private Date afterDays(int days){
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.DATE, days);  // number of days to add 
		return calendar.getTime();
	}

}
