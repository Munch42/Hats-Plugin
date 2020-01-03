package me.munch42.hats;

import me.munch42.hats.commands.HatsCommand;
import me.munch42.hats.inventories.HatGUI;
import me.munch42.hats.listeners.HatGUIListener;
import me.munch42.hats.listeners.PlayerJoinListener;
import me.munch42.hats.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Hats extends JavaPlugin {

    private static Hats plugin;

    private File playersFile = new File(getDataFolder(), "players.yml");
    private FileConfiguration playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    // Player UUID, Page
    public static Map<UUID, Integer> playerPages = new HashMap<>();
    // Page Number, GUI
    public static Map<Integer, HatGUI> hatGUIS = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if(!playersFile.exists()){
            saveResource("players.yml", false);
        }

        loadListeners();
        loadCommands();
        loadGUIs();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Hats] Hats version: " + getDescription().getVersion() + " is now enabled!");
        plugin = this;
    }

    public void saveConfig(){
        try{
            getPlayersConfig().save(getPlayersFile());
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "[Hats] Hats version: " + getDescription().getVersion() + " is now disabled!");
    }

    private void loadListeners(){
        new HatGUIListener(this);
        new PlayerJoinListener(this);
    }

    private void loadCommands(){
        new HatsCommand(this);
    }

    private void loadGUIs(){
        // Each page can have a max of 6 rows. 1 row is reserved for navigation so 5 rows total of hats.
        ConfigurationSection hats = getConfig().getConfigurationSection("hats");

        /*for (String key : hats.getKeys(false))
        {

        }*/

        int totalHats = hats.getKeys(false).size();
        int totalRows = (int) Math.ceil((float) totalHats / 9);
        if(totalRows == 0){ totalRows = 1; }
        int totalPages = (int) Math.ceil((float) totalRows / 5);
        if(totalPages == 0){ totalPages = 1; }

        for(int i = 1; i <= totalPages; i++){
            HatGUI gui = new HatGUI(54, i, totalPages);
            hatGUIS.put(i, gui);
        }

        // Problem may be that it goes through on 1 page and then loops through the same items again for the second page.

        int hatsDone = 0;
        int onPage = 1;
        HatGUI gui = hatGUIS.get(onPage);
        for(String key : hats.getKeys(false)){

            if(hatsDone > 44){
                onPage++;
                hatsDone = 0;
                gui = hatGUIS.get(onPage);
            }

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

            gui.setItem(hatsDone, item, player -> {
                if(player.getInventory().getHelmet() != null){
                    if(!getConfig().getString("wearingHatMessage").equals("")){
                        String message = getConfig().getString("wearingHatMessage");
                        message = ChatUtils.parseColourCodes(message);
                        player.sendMessage(message);
                    }
                    return;
                }
                if(!hats.getString(key + ".permission").equals("")){
                    if(player.hasPermission(hats.getString( key + ".permission"))){
                        equipHelmet(item, player, key);
                    } else {
                        if(!getConfig().getString("noHatPerm").equals("")){
                            String message = getConfig().getString("noHatPerm");
                            message = message.replace("%perm%", hats.getString(key + ".permission"));
                            message = ChatUtils.parseColourCodes(message);
                            player.sendMessage(message);
                        }
                    }
                } else {
                    equipHelmet(item, player, key);
                }
            });
            hatsDone++;
        }
    }

    private void equipHelmet(ItemStack item, Player player, String key){
        player.getInventory().setHelmet(item);
        player.closeInventory();
        getPlayersConfig().set("players." + player.getUniqueId() + ".hat", true);
        saveConfig();
        if(!getConfig().getString("hatEquipMessage").equals("")){
            String message = getConfig().getString("hatEquipMessage");
            if(getConfig().getString("hats." + key + ".displayName").equals("")){
                String itemName = getConfig().getString("hats." + key + ".material");
                message = message.replace("%hat%", ChatUtils.parseMaterialToDisplayName(itemName));
            } else {
                message = message.replace("%hat%", item.getItemMeta().getDisplayName());
            }
            message = ChatUtils.parseColourCodes(message);
            player.sendMessage(message);
        }
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }

    public static Map<UUID, Integer> getPlayerPages() {
        return playerPages;
    }

    public File getPlayersFile(){
        return playersFile;
    }

    public static Hats getPlugin(){
        return plugin;
    }
}
