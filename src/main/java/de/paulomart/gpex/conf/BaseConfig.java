package de.paulomart.gpex.conf;

import java.io.File;

import lombok.Getter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import static de.paulomart.gpex.utils.ConfigUtils.*;

@Getter
/**
 * Base functions for a config.
 * @author Paul
 *
 */
public abstract class BaseConfig {
	
	private JavaPlugin plugin;
	private File configFile;
	protected FileConfiguration config;
	private String defaultResource;
	private boolean copyDefaults;
	private boolean saveOnLoad;
	
	/**
	 * Uses the Absolute Path
	 * @param plugin The Handler Plugin
	 * @param configFile Absolute Path of Config
	 * @param defaultResource If you dont want to load set null
	 * @param copyDefaults
	 */
	public BaseConfig(JavaPlugin plugin, File configFile, String defaultResource, boolean copyDefaults, boolean saveOnLoad){
		this.plugin = plugin;
		this.configFile = configFile;
		this.copyDefaults = copyDefaults;
		this.defaultResource = defaultResource;
		this.saveOnLoad = saveOnLoad;
		config = YamlConfiguration.loadConfiguration(configFile);
		config.options().copyDefaults(this.copyDefaults);	
	}

	/**
	 * The file in the Plugin Dir
	 * @param plugin The Handler Plugin
	 * @param configFile The file in the Plugin Dir
	 * @param defaultResource If you dont want to load set null
	 * @param copyDefaults
	 */
	public BaseConfig(JavaPlugin plugin, String fileName, String defaultResource, boolean copyDefaults, boolean saveOnLoad){
		this(plugin, new File(plugin.getDataFolder(), fileName), defaultResource, copyDefaults, saveOnLoad);
	}	
	
	/**
	 * Loads the config form configfile, loads the defaults out of the defaultResource
	 */
	public void load(){
		hardLoad(config, configFile);
		if (defaultResource != null){
			loadResource(plugin, defaultResource, config);
		}
		if (saveOnLoad){
			hardSave(config, configFile);
		}
		onLoad();
	}
	
	public void save(){
		onSave();
		hardSave(config, configFile);
	}
	
	/**
	 * Load here all the vars form the YamlConfig you need
	 */
	public abstract void onLoad();
	
	/**
	 * Save here all the vars into the YamlConfig you want to save
	 */
	public abstract void onSave();
	
}
