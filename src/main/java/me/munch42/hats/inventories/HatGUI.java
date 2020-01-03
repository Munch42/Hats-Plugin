package me.munch42.hats.inventories;

import me.munch42.hats.Hats;
import me.munch42.hats.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class HatGUI extends BaseHatGUI {
    public HatGUI(int invSize, int pageNum, int totalPages){
        super(invSize, "Hats");

        for(int i = invSize - 9; i < invSize; i++){
            if(i == invSize - 9){
                ItemStack redstoneBlock = new ItemStack(Material.REDSTONE_BLOCK,1);
                ItemMeta meta = redstoneBlock.getItemMeta();
                meta.setDisplayName("Remove Your Hat");
                redstoneBlock.setItemMeta(meta);
                setItem(i, redstoneBlock, player -> {
                    // Remove Player Hat
                    if(Hats.getPlugin().getPlayersConfig().getBoolean("players." + player.getUniqueId() + ".hat")){
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                        Hats.getPlugin().getPlayersConfig().set("players." + player.getUniqueId() + ".hat", false);
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
                });
            } else if(i == invSize - 6){
                ItemStack arrow = new ItemStack(Material.ARROW,1);
                ItemMeta meta = arrow.getItemMeta();
                meta.setDisplayName("<< Previous Page");
                arrow.setItemMeta(meta);
                setItem(i, arrow, player -> {
                    // Go back a page
                    UUID puuid = player.getUniqueId();
                    int page;
                    page = Hats.playerPages.get(puuid);
                    if(!((page - 1) <= 0)){
                        page -= 1;
                    } else {
                        page = totalPages;
                    }
                    Hats.playerPages.put(puuid, page);
                    player.closeInventory();
                    Hats.hatGUIS.get(page).open(player);
                    Hats.playerPages.remove(puuid);
                });
            } else if(i == invSize - 5){
                ItemStack barrier = new ItemStack(Material.BARRIER,1);
                ItemMeta meta = barrier.getItemMeta();
                meta.setDisplayName("Close Menu");
                ArrayList<String> lore = new ArrayList<>();
                lore.add("Page " + pageNum + "/" + totalPages);
                meta.setLore(lore);
                barrier.setItemMeta(meta);
                setItem(i, barrier, player -> {
                    Hats.playerPages.remove(player.getUniqueId());
                    player.closeInventory();
                });
            } else if(i == invSize - 4){
                ItemStack arrow = new ItemStack(Material.ARROW,1);
                ItemMeta meta = arrow.getItemMeta();
                meta.setDisplayName("Next Page >>");
                arrow.setItemMeta(meta);
                setItem(i, arrow, player -> {
                    // Go to next page
                    UUID puuid = player.getUniqueId();
                    int page;
                    page = Hats.playerPages.get(puuid);
                    if(!((page + 1) > totalPages)){
                        page += 1;
                    } else {
                        page = 1;
                    }
                    Hats.playerPages.put(puuid, page);
                    player.closeInventory();
                    Hats.hatGUIS.get(page).open(player);
                    Hats.playerPages.remove(puuid);
                });
            } else {
                setItem(i, new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1));
            }

        }
    }

}
