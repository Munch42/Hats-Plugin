package me.munch42.hats.listeners;

import me.munch42.hats.Hats;
import me.munch42.hats.inventories.BaseHatGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class HatGUIListener implements Listener {

    private Hats plugin;

    public HatGUIListener(Hats plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }

        Player p = (Player) event.getWhoClicked();
        UUID playerUUID = p.getUniqueId();

        UUID inventoryUUID = BaseHatGUI.openInventories.get(playerUUID);
        if(inventoryUUID != null){
            event.setCancelled(true);
            BaseHatGUI gui = BaseHatGUI.getInventoriesByUUID().get(inventoryUUID);
            BaseHatGUI.hatGUIActions action = gui.getActions().get(event.getSlot());

            if(action != null){
                action.click(p);
            }
        }

        if(event.getRawSlot() == 5){
            if(plugin.getPlayersConfig().getBoolean("players." + p.getUniqueId() + ".hat")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player p = (Player) event.getPlayer();
        UUID playerUUID = p.getUniqueId();

        BaseHatGUI.openInventories.remove(playerUUID);
        Hats.playerPages.remove(playerUUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player p = event.getPlayer();
        UUID playerUUID = p.getUniqueId();

        BaseHatGUI.openInventories.remove(playerUUID);
        Hats.playerPages.remove(playerUUID);
    }
}
