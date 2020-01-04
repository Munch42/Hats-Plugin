package me.munch42.hats.listeners;

import me.munch42.hats.Hats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDeathListener implements Listener {

    private Hats plugin;

    public PlayerDeathListener(Hats plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event){

        Player p = event.getEntity();

        if(plugin.getPlayersConfig().getBoolean("players." + p.getUniqueId() + ".hat")){
            ConfigurationSection hats = plugin.getConfig().getConfigurationSection("hats");

            for(ItemStack i : event.getDrops()){
                for(String key : hats.getKeys(false)){
                    if(i.getItemMeta().getLore().equals(Hats.convertArrayToList(hats.getString(key + ".lore").split(";")))){
                        if(i.getItemMeta().getDisplayName().equals(hats.getString(key + ".displayName"))){
                            if(i.getType().equals(Material.getMaterial(hats.getString(key + ".material")))){
                                event.getDrops().remove(i);
                            }
                        }
                    }
                }
            }
        }
    }
}
