package de.paulomart.gpex.tag;

import org.bukkit.entity.Player;

/**
 * Does nothing.
 * @author Paul
 *
 */
public class NoNameTagChangeImplemention implements GPexNameTagManager{

	@Override
	public void setNameTag(Player player, String prefix, String suffix) {}

	@Override
	public void removeNameTag(Player player) {}

}
