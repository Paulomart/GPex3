package de.paulomart.gpex.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utilities for the YamlConfig absolute save
 * @author Paul
 *
 */
public class ConfigUtils {

	/**
	 * Loads an ItemStack you of the config
	 * @param config
	 * @param path
	 * @return ItemStack
	 */
	public static ItemStack loadItemStack(FileConfiguration config, String path){
		Map<String, Object> serialized = new HashMap<String, Object>();
		for (String subPath : config.getConfigurationSection(path).getKeys(true)){
			serialized.put(subPath, config.get(path+"."+subPath));
		}
		return ItemStack.deserialize(serialized);
	}
	
	/**
	 * Saves an ItemStack into the config
	 * @param config
	 * @param path
	 * @param itemStack
	 */
	public static void saveItemStack(FileConfiguration config, String path, ItemStack itemStack){
		Map<String, Object> serialized = itemStack.serialize();
		for (String subPath : serialized.keySet()){
			config.set(path+"."+subPath, serialized.get(subPath));
		}
	}
	
	public static void saveLocations(FileConfiguration config, String path, List<Location> locations){
		int i = 0;
		for (Location location : locations){
			saveLocation(config, path+"."+String.valueOf(i), location);
			i++;
		}
	}
	
	public static List<Location> loadLocations(FileConfiguration config, String path, World world){
		List<Location> locations = new ArrayList<Location>();
		for (String key : config.getConfigurationSection(path).getKeys(false)){
			locations.add(loadLocation(config, path+"."+key, world));
		}
		return locations;
	}
	
	public static List<Location> loadLocations(FileConfiguration config, String path){
		List<Location> locations = new ArrayList<Location>();
		for (String key : config.getConfigurationSection(path).getKeys(false)){
			locations.add(loadLocation(config, path+"."+key));
		}
		return locations;
	}
	
	/**
	 * Loads a location out of the Config
	 * @param path Path in the Configfile
	 * @return Location
	 */
	public static Location loadLocation(FileConfiguration config, String path, World world){
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		float yaw	= (float) config.getDouble(path + ".yaw");
		float pitch = (float) config.getDouble(path + ".pitch");
			
		Location loc = new Location(world, x, y, z);
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		
		return loc;
	}
	
	/**
	 * Loads a location out of the Config
	 * @param path Path in the Configfile
	 * @return Location
	 */
	public static Location loadLocation(FileConfiguration config, String path){
		World world = Bukkit.getServer().getWorld(config.getString(path + ".world"));
		if (world == null){
			world = Bukkit.getServer().getWorlds().get(0);
		}
		
		return loadLocation(config, path, world);
	}
	
	/**
	 * Saves a Location into the Config
	 * @param path Path in the Configfile
	 * @param loc Location
	 */
	public static void saveLocation(FileConfiguration config, String path, Location loc){		
		config.set(path+ ".x", loc.getX());
		config.set(path+ ".y", loc.getY());
		config.set(path+ ".z", loc.getZ());
		config.set(path+ ".yaw", loc.getYaw());
		config.set(path+ ".pitch", loc.getPitch());
		config.set(path+ ".world", loc.getWorld().getName());
	}
	
	/**
	 * Loads a Config form the local disk.
	 * @param config Config that will be loaded
	 * @param file File out that the Config will be loaded
	 */
	public static boolean hardLoad(FileConfiguration config, File file){
		if (!file.exists()){
			return false;
		}
		try {
			config.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Saves a Config to the local disk.
	 * @param config Config that will be saved
	 * @param file File in that the Config will be Saved
	 */
	public static boolean hardSave(FileConfiguration config, File file){
		try {
			config.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Loads default values form a resource out of the jar in to a Config
	 * @param resource Name of the Resource that will be used
	 * @param config Config that will get the default values
	 */
	public static boolean loadResource(JavaPlugin srcPlugin, String resource, FileConfiguration config){
		InputStream inputStream;
		if ((inputStream = srcPlugin.getResource(resource)) == null){
			return false;
		}
		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(inputStream);
		config.setDefaults(defaultConfig);
		return true;
	}
	
}
