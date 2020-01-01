package me.munch42.hats.listeners;

import me.munch42.hats.Hats;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private Hats plugin;

    public PlayerJoinListener(Hats plugin){
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        // Save new player data on player join
        if(!plugin.getPlayersConfig().contains("players." + event.getPlayer().getUniqueId())){
            plugin.getPlayersConfig().set("players." + event.getPlayer().getUniqueId() + ".name", event.getPlayer().getDisplayName());
            plugin.getPlayersConfig().set("players." + event.getPlayer().getUniqueId() + ".hat", false);
            plugin.saveConfig();
        }

        // Save New Display Name if name changed
        if(!plugin.getPlayersConfig().getString("players." + event.getPlayer().getUniqueId() + ".name").equals(event.getPlayer().getDisplayName())){
            plugin.getPlayersConfig().set("players." + event.getPlayer().getUniqueId() + ".name", event.getPlayer().getDisplayName());
            plugin.saveConfig();
        }
    }
}
