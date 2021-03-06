package me.munch42.hats;

import me.munch42.hats.commands.HatsCommand;
import me.munch42.hats.inventories.HatGUI;
import me.munch42.hats.listeners.HatGUIListener;
import me.munch42.hats.listeners.PlayerDeathListener;
import me.munch42.hats.listeners.PlayerJoinListener;
import me.munch42.hats.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

        plugin = this;

        loadListeners();
        loadCommands();
        loadGUIs();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Hats] Hats version: " + getDescription().getVersion() + " is now enabled!");
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
        new PlayerDeathListener(this);
    }

    private void loadCommands(){
        new HatsCommand(this);
    }

    private void loadGUIs(){
        // Each page can have a max of 6 rows. 1 row is reserved for navigation so 5 rows total of hats.
        ConfigurationSection hats = getConfig().getConfigurationSection("hats");
        String title;

        /*for (String key : hats.getKeys(false))
        {

        }*/

        if(!getConfig().getString("guiTitle").equals("")){
            title = ChatUtils.parseColourCodes(getConfig().getString("guiTitle"));
        } else {
            title = "Hats";
        }

        int totalHats = hats.getKeys(false).size();
        int totalRows = (int) Math.ceil((float) totalHats / 9);
        if(totalRows == 0){ totalRows = 1; }
        int totalPages = (int) Math.ceil((float) totalRows / 5);
        if(totalPages == 0){ totalPages = 1; }

        for(int i = 1; i <= totalPages; i++){
            HatGUI gui = new HatGUI(54, i, totalPages, title);
            hatGUIS.put(i, gui);
        }

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

            //System.out.println(hats.getString(key + ".job"));

            /*if(hats.getString(key + ".job") != null){
                boolean blank = false;
                String job = hats.getString(key + ".job");
                System.out.println(job);

                switch (job) {
                    case "":
                        blank = true;
                        System.out.println("Blank");
                        break;
                    case "REMOVE":
                        gui.setItem(hatsDone, item, player -> {
                            Hats.getPlugin().removeFunction(player);
                        });
                        System.out.println("Remove");
                        break;
                    case "BACK":
                        int finalTotalPages = totalPages;
                        gui.setItem(hatsDone, item, player -> {
                            Hats.getPlugin().backFunction(player, finalTotalPages);
                        });
                        System.out.println("Back");
                        break;
                    case "FORWARD":
                        int finalTotalPages1 = totalPages;
                        gui.setItem(hatsDone, item, player -> {
                            Hats.getPlugin().nextFunction(player, finalTotalPages1);
                        });
                        System.out.println("Forward");
                        break;
                    case "CLOSE":
                        gui.setItem(hatsDone, item, player -> {
                            Hats.getPlugin().closeFunction(player);
                        });
                        System.out.println("Close");
                        break;
                }
                if(!blank){
                    continue;
                }
            }*/

            //System.out.println("Past should be?: " + hats.getString(key + ".job"));
            gui.setItem(hatsDone, item, player -> {
                //System.out.println("Past should be2?: " + hats.getString(key + ".job"));
                if(player.getInventory().getHelmet() != null){
                    if(!getPlayersConfig().getBoolean("players." + player.getUniqueId() + ".hat")){
                        if(!getConfig().getString("wearingHatMessage").equals("")){
                            String message = getConfig().getString("wearingHatMessage");
                            message = ChatUtils.parseColourCodes(message);
                            player.sendMessage(message);
                        }
                        return;
                    }
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
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        player.getInventory().setHelmet(item);
        player.closeInventory();
        getPlayersConfig().set("players." + player.getUniqueId() + ".hat", true);
        getPlayersConfig().set("players." + player.getUniqueId() + ".hatKey", key);
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

    public void removeFunction(Player player){
        // Remove Player Hat
        if(Hats.getPlugin().getPlayersConfig().getBoolean("players." + player.getUniqueId() + ".hat")){
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
            Hats.getPlugin().getPlayersConfig().set("players." + player.getUniqueId() + ".hat", false);
            Hats.getPlugin().getPlayersConfig().set("players." + player.getUniqueId() + ".hatKey", "null");
            Hats.getPlugin().saveConfig();
            if(!Hats.getPlugin().getConfig().getString("hatRemoved").equals("")){
                String message = Hats.getPlugin().getConfig().getString("hatRemoved");
                message = ChatUtils.parseColourCodes(message);
                player.sendMessage(message);
                player.closeInventory();
            }
        } else {
            if(!Hats.getPlugin().getConfig().getString("errorRemove").equals("")){
                String message = Hats.getPlugin().getConfig().getString("errorRemove");
                message = ChatUtils.parseColourCodes(message);
                player.sendMessage(message);
                player.closeInventory();
            }
        }
    }

    public void backFunction(Player player, int totalPages){
        // Go back a page
        UUID puuid = player.getUniqueId();
        int page = 1;
        page = Hats.playerPages.get(puuid);
        if(!(page <= 1)){
            page -= 1;
        } else {
            page = totalPages;
        }
        Hats.playerPages.remove(puuid);
        Hats.playerPages.put(puuid, page);
        player.closeInventory();
        Hats.hatGUIS.get(page).open(player);
    }

    public void closeFunction(Player player){
        Hats.playerPages.remove(player.getUniqueId());
        player.closeInventory();
    }

    public void nextFunction(Player player, int totalPages){
        // Go to next page
        UUID puuid = player.getUniqueId();
        int page = 1;
        page = Hats.playerPages.get(puuid);
        if(!(page >= totalPages)){
            page += 1;
        } else {
            page = 1;
        }
        Hats.playerPages.remove(puuid);
        Hats.playerPages.put(puuid, page);
        player.closeInventory();
        Hats.hatGUIS.get(page).open(player);
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
