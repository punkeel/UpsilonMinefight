package fr.ungeek.Upsilon.events;

import fr.ungeek.Upsilon.Main;
import fr.ungeek.Upsilon.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChangeMenuEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	boolean isAsync;
	private MenuManager.Menus new_menu;
	private Player p;

	public ChangeMenuEvent(MenuManager.Menus nm, Player player) {
		new_menu = nm;
		p = player;
		isAsync = true;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public MenuManager.Menus getNew_menu() {
		return new_menu;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return p;
	}
}