package fr.ungeek.Upsilon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.List;

/**
 * User: PunKeel
 * Date: 5/9/13
 * Time: 6:43 PM
 * May be open-source & be sold (by PunKeel, of course !)
 */
public class MoneyListener implements Listener {
	Main main;
	ItemStack emerald;
	HashMap<String, Integer> selling = new HashMap<String, Integer>();

	public MoneyListener(Main m) {
		main = m;
		emerald = m.nameItem(new ItemStack(Material.EMERALD), ChatColor.GREEN + "Menu principal", ChatColor.GRAY + "(Clic droit pour ouvrir)");

	}

	@EventHandler()
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		String today = main.getDate();
		String yesterday = main.getDate(-1);
		final String message;
		if (p.hasMetadata("ups_lastjoin")) {
			List<MetadataValue> ups_lastjoin = p.getMetadata("ups_lastjoin");
			String LastJoin = "";
			if (ups_lastjoin.size() != 0) {
				LastJoin = p.getMetadata("ups_lastjoin").get(0).asString();
			}
			if (!LastJoin.equals(today)) {
				int jours = 1;
				p.removeMetadata("ups_lastjoin", main);
				p.removeMetadata("ups_follow", main);
				p.setMetadata("ups_lastjoin", new FixedMetadataValue(main, today));
				if (LastJoin.equals(yesterday)) {
					// consecutif
					jours = p.getMetadata("ups_follow").get(0).asInt() + 1;
					int gain = ((jours > 5) ? 50 : (jours * 10));
					p.setMetadata("ups_follow", new FixedMetadataValue(main, jours));
					message = (main.getTAG() + "Tu as reçu " + gain + " ƒ pour tes " + jours + " jours de présence à la suite !");
					main.econ.depositPlayer(p.getName(), gain);
				} else {
					// pas consecutif
					p.setMetadata("ups_follow", new FixedMetadataValue(main, jours));
					message = main.getTAG() + "Tu as reçu 10 ƒ pour ton premier jour de présence consécutif!";
					main.econ.depositPlayer(p.getName(), 10);
				}
				if (!message.isEmpty()) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
						public void run() {
							p.sendMessage(message);
						}
					}, 20);
				}
			}

		} else {
			p.setMetadata("ups_lastjoin", new FixedMetadataValue(main, today));
			p.setMetadata("ups_follow", new FixedMetadataValue(main, 1));
		}

		if (!p.getInventory().containsAtLeast(emerald, 1)) {
			if (!p.getEnderChest().containsAtLeast(emerald, 1)) {
				if (p.getInventory().firstEmpty() != -1) {
					p.getInventory().addItem(emerald);
				} else {
					if (p.getEnderChest().firstEmpty() != -1) {
						p.getInventory().addItem(emerald);
					} else {
						p.sendMessage(main.getTAG() + "Ton inventaire est plein, vide le un peu et tape /menu pour avoir une émeraude ! :)");
					}
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (e.getItemDrop().getItemStack().isSimilar(emerald) && !main.isAdmin(p)) {
			e.setCancelled(true);
			return;
		}
		if (isSelling(p.getName()) && e.getItemDrop().getItemStack().getType().equals(Material.FLINT)) {

			main.econ.depositPlayer(p.getName(), e.getItemDrop().getItemStack().getAmount() * 5);
			p.sendMessage(main.TAG + ChatColor.DARK_GREEN + "+" + ChatColor.RESET + e.getItemDrop().getItemStack().getAmount() * 5 + "ƒ pour la vente du silex " + ChatColor.GRAY + "(Flint)");
			ItemStack i = new ItemStack(Material.AIR);
			i.setAmount(0);
			e.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			if (e.getItem() == null) return;
			if (e.getItem().getType().equals(Material.FLINT)) {
				selling.put(p.getName(), main.getTimestamp());
				p.sendMessage(main.TAG + "Jette tes silex " + ChatColor.GRAY + "(Flint)" + ChatColor.RESET + " pour les vendre ! (Tu as 10 secondes)");

			}
		}
	}

	public boolean isSelling(String name) {
		if (!selling.containsKey(name))
			return false;
		if (main.getTimestamp() - selling.get(name) > 10)
			return false;
		return true;
	}
}