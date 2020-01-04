package me.munch42.hats.listeners;

import me.munch42.hats.Hats;
import me.munch42.hats.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class PlayerDeathListener implements Listener {

    private Hats plugin;

    public PlayerDeathListener(Hats plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private ItemStack getHatItem(Player p){
        ConfigurationSection hats = plugin.getConfig().getConfigurationSection("hats");
        String key = plugin.getPlayersConfig().getString("players." + p.getUniqueId() + ".hatKey");

        ItemStack item = new ItemStack(Material.getMaterial(hats.getString(key + ".material")));
        ItemMeta meta = item.getItemMeta();
        if(!hats.getString(key + ".displayName").equals("")){
            meta.setDisplayName(ChatUtils.parseColourCodes(hats.getString(key + ".displayName")));
        }

        if(!hats.getString( key + ".lore").equals("")){
            ArrayList<String> lore = new ArrayList<>();
            String[] loreList = hats.getString(key + ".lore").split(";");
            for(String loreString : loreList){
                lore.add(ChatUtils.parseColourCodes(loreString));
            }
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event){

        Player p = event.getEntity();

        if(plugin.getPlayersConfig().getBoolean("players." + p.getUniqueId() + ".hat")){
            ItemStack item = getHatItem(p);
            event.getDrops().remove(item);
        }
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event){
        Player p = event.getPlayer();

        if(plugin.getPlayersConfig().getBoolean("players." + p.getUniqueId() + ".hat")){
            ItemStack item = getHatItem(p);
            p.getInventory().setHelmet(item);
        }
    }
}
