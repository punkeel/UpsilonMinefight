package fr.ungeek.Upsilon.Menus;

import fr.ungeek.Upsilon.Main;
import fr.ungeek.Upsilon.MenuManager;
import fr.ungeek.Upsilon.events.ChangeMenuEvent;
import fr.ungeek.Upsilon.events.MenuClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: PunKeel
 * Date: 5/5/13
 * Time: 8:00 PM
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class EventMenu implements Listener {
	Main m;
	MenuManager MM;
	HashMap<String, Boolean> warps = new HashMap<String, Boolean>();
	List<String> slots = new ArrayList<String>();
	private ItemStack dac, hungerfight, sauta, sautb, color, pvp1v1, lune, vitesse, skin;

	public EventMenu(Main main, MenuManager MM) {
		m = main;
		this.MM = MM;
		warps.put("dac", true); // echelle
		warps.put("hungerfight", true); // hungerfight
		warps.put("saut1", true);         // popo
		warps.put("saut2", true);          // popo
		warps.put("pvp1v1", true); // épée
		warps.put("color", true); // laine
		warps.put("vitesse", true); // popo swift
		warps.put("skin", true); // tete de steve
		warps.put("lune", true); // ender stone

		slots.add("dac");
		slots.add("hungerfight");
		slots.add("saut1");
		slots.add("saut2");
		slots.add("pvp1v1");
		slots.add("color");
		slots.add("vitesse");
		slots.add("skin");
		slots.add("lune");

	}

	public void generateItems() {
		String lore_off = ChatColor.DARK_RED + "Event indisponible";
		String lore_on = ChatColor.GREEN + "Cliquez pour rejoindre";

		ItemStack jump_potion = new ItemStack(Material.POTION);
		PotionMeta p_meta = (PotionMeta) jump_potion.getItemMeta();
		p_meta.setMainEffect(PotionEffectType.JUMP);
		dac = m.nameItem(new ItemStack(Material.LADDER), ChatColor.DARK_AQUA + "Event DAC", warps.get("dac") ? lore_on : lore_off);
		hungerfight = m.nameItem(new ItemStack(Material.COOKED_BEEF), ChatColor.DARK_AQUA + "Event HungerFight", warps.get("hungerfight") ? lore_on : lore_off);
		sauta = m.nameItem(jump_potion.clone(), ChatColor.DARK_AQUA + "Event Saut 1", warps.get("saut1") ? lore_on : lore_off);
		sautb = m.nameItem(jump_potion.clone(), ChatColor.DARK_AQUA + "Event Saut 2", warps.get("saut2") ? lore_on : lore_off);
		pvp1v1 = m.nameItem(new ItemStack(Material.STONE_SWORD), ChatColor.DARK_AQUA + "Event PVP", warps.get("pvp1v1") ? lore_on : lore_off);
		color = m.nameItem(new ItemStack(Material.WOOL, 1, (short) 5), ChatColor.DARK_AQUA + "Event Color", warps.get("color") ? lore_on : lore_off);
		vitesse = m.nameItem(new ItemStack(Material.POTION, 1, (short) 8258), ChatColor.DARK_AQUA + "Event Vitesse", warps.get("vitesse") ? lore_on : lore_off);
		skin = m.nameItem(new ItemStack(Material.getMaterial(397), 1, (short) 3), ChatColor.DARK_AQUA + "Event Skins", warps.get("skin") ? lore_on : lore_off);
		lune = m.nameItem(new ItemStack(Material.ENDER_STONE), ChatColor.DARK_AQUA + "Event Lune", warps.get("lune") ? lore_on : lore_off);
	}

	public HashMap<String, Boolean> getWarps() {
		return warps;
	}

	public void loadWarps() {
		for (String s : warps.keySet()) {
			if (!m.getConfig().contains("events." + s)) continue;
			warps.put(s, m.getConfig().getBoolean("events." + s, false));
		}
		generateItems();
	}

	public Boolean changeState(String name, Boolean state, CommandSender by) {
		if (!warps.containsKey(name)) {
			return false;
		} else {
			if (!warps.get(name).equals(state)) {
				warps.put(name, state);
				generateItems();
			}
			return true;
		}
	}

	public MenuManager.Menus getSelfMenuType() {
		return MenuManager.Menus.EVENTS;
	}

	@EventHandler
	public void onMenuOpen(ChangeMenuEvent e) {
		if (!e.getNew_menu().equals(getSelfMenuType())) return;
		MM.current_menu.put(e.getPlayer().getName(), getSelfMenuType());

		Inventory inv = Bukkit.createInventory(null, 9, "Menu > Téléportation > Events");
		inv.setItem(0, dac);
		inv.setItem(1, hungerfight);
		inv.setItem(2, sauta);
		inv.setItem(3, sautb);
		inv.setItem(4, pvp1v1);
		inv.setItem(5, color);
		inv.setItem(6, vitesse);
		inv.setItem(7, skin);
		inv.setItem(8, lune);

		e.getPlayer().openInventory(inv);
	}

	@EventHandler
	public void onMenuClick(MenuClickEvent e) {
		if (!e.getCurrent_menu().equals(getSelfMenuType())) return;
		String warp = String.valueOf(slots.get(e.getEvent().getSlot()));
		if (warp.isEmpty()) return;
		Player p = (Player) e.getEvent().getWhoClicked();
		if (warps.get(warp)) {
			MM.closeInventory(this.getSelfMenuType(), p);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("warp %s %s", warp, p.getName()));
		} else {
			p.sendMessage(m.getTAG() + "Cet événement est fermé pour le moment");
			return;
		}

	}
}