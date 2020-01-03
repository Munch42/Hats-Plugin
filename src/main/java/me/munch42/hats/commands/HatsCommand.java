package me.munch42.hats.commands;

import me.munch42.hats.Hats;
import me.munch42.hats.inventories.HatGUI;
import me.munch42.hats.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class HatsCommand implements CommandExecutor {

    private Hats plugin;

    public HatsCommand(Hats plugin){
        this.plugin = plugin;

        plugin.getCommand("hats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.DARK_RED + "You must be a player to use this command!");
            return true;
        }

        Player p = (Player) sender;

        if(!p.hasPermission(plugin.getConfig().getString("guiOpenPerm"))){
            if(!plugin.getConfig().getString("noPermMessage").equals("")) {
                p.sendMessage(ChatUtils.parseColourCodes(plugin.getConfig().getString("noPermMessage")));
            }
            return true;
        }

        /*for (String key : hats.getKeys(false))
        {

        }*/

        HatGUI gui = Hats.hatGUIS.get(1);
        Hats.playerPages.put(p.getUniqueId(), 1);
        gui.open(p);
        
        return true;
    }
}
