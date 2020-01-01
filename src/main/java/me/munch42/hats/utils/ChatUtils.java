package me.munch42.hats.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static void broadcast(String msg){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public static String parseColourCodes(String msg){
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }

    public static String parseMaterialToDisplayName(String material){
        String itemDisplayName = material.replace("_", " ");
        itemDisplayName = itemDisplayName.toLowerCase();
        itemDisplayName = WordUtils.capitalize(itemDisplayName);
        return itemDisplayName;
    }
}
