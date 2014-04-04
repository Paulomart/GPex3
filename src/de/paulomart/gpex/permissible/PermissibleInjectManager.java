package de.paulomart.gpex.permissible;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permissible;

import de.paulomart.gpex.GPex;
import static de.paulomart.gpex.utils.CraftBukkitInterface.getCBClassName;

/**
 * @author permissionsex, modifyed by paulomart
 */
public class PermissibleInjectManager {
	
	private GPex gpex;
	
	public PermissibleInjectManager() {
		gpex = GPex.getInstance();
		Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), gpex);
		injectAllPermissibles();
	}

	protected static final PermissibleInjector[] injectors = new PermissibleInjector[] {
		new PermissibleInjector.ClassPresencePermissibleInjector("net.glowstone.entity.GlowHumanEntity", "permissions", true),
		new PermissibleInjector.ClassPresencePermissibleInjector("org.getspout.server.entity.SpoutHumanEntity", "permissions", true),
		new PermissibleInjector.ClassNameRegexPermissibleInjector("org.getspout.spout.player.SpoutCraftPlayer", "perm", false, "org\\.getspout\\.spout\\.player\\.SpoutCraftPlayer"),
		new PermissibleInjector.ClassPresencePermissibleInjector(getCBClassName("entity.CraftHumanEntity"), "perm", true),
	};

	public void onDisable() {
		uninjectAllPermissibles();
	}

	public void injectPermissible(Player player) {
		try {
			PermissibleGPex permissible = new PermissibleGPex(player);

			boolean success = false, found = false;
			for (PermissibleInjector injector : injectors) {
				if (injector.isApplicable(player)) {
					found = true;
					Permissible oldPerm = injector.inject(player, permissible);
					if (oldPerm != null) {
						permissible.setPreviousPermissible(oldPerm);
						success = true;
						break;
					}
				}
			}

			if (!found) {
				gpex.getLogger().warning("No Permissible injector found for your server implementation!");
			} else if (!success) {
				gpex.getLogger().warning("Unable to inject GPEX's permissible for " + player.getName());
			}

		} catch (Throwable e) {
			gpex.getLogger().log(Level.SEVERE, "Unable to inject permissible for " + player.getName(), e);
		}
	}

	private void injectAllPermissibles() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			injectPermissible(player);
		}
	}

	private void uninjectPermissible(Player player) {
		try {
			boolean success = false;
			for (PermissibleInjector injector : injectors) {
				if (injector.isApplicable(player)) {
					Permissible pexPerm = injector.getPermissible(player);
					if (pexPerm instanceof PermissibleGPex) {
						if (injector.inject(player, ((PermissibleGPex) pexPerm).getPreviousPermissible()) != null) {
							success = true;
							break;
						}
					}
				}
			}

			if (!success) {
				gpex.getLogger().warning("No Permissible injector found for your server implementation (while uninjecting for " + player.getName() + "!");
			} 
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void uninjectAllPermissibles() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			uninjectPermissible(player);
		}
	}

	private class EventListener implements Listener {
		@EventHandler(priority = EventPriority.LOWEST)
		public void onPlayerLogin(PlayerLoginEvent event) {
			injectPermissible(event.getPlayer());
		}

		@EventHandler(priority = EventPriority.MONITOR)
		// Technically not supposed to use MONITOR for this, but we don't want to remove before other plugins are done checking permissions
		public void onPlayerQuit(PlayerQuitEvent event) {
			uninjectPermissible(event.getPlayer());
		}
	}

}