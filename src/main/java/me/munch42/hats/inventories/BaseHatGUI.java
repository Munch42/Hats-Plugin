package me.munch42.hats.inventories;

import me.munch42.hats.Hats;
import me.munch42.hats.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseHatGUI {
    private UUID uuid;
    private Inventory baseHatGUI;
    private Map<Integer, hatGUIActions> actions;
    public static Map<UUID, BaseHatGUI> inventoriesByUUID = new HashMap<>();
    public static Map<UUID, UUID> openInventories = new HashMap<>();

    public BaseHatGUI(int invSize, String invName){
        uuid = UUID.randomUUID();
        baseHatGUI = Bukkit.createInventory(null, invSize, invName);
        actions = new HashMap<>();
        inventoriesByUUID.put(getUuid(), this);
    }

    public Inventory getBaseHatGUI(){
        return baseHatGUI;
    }

    public void open(Player p){
        p.openInventory(baseHatGUI);
        openInventories.put(p.getUniqueId(), getUuid());
    }

    public void setItem(int slot, ItemStack stack, hatGUIActions action){
        baseHatGUI.setItem(slot, stack);
        if(action != null){
            actions.put(slot, action);
        }
    }

    public void setItem(int slot, ItemStack stack){
        setItem(slot, stack, null);
    }

    public void delete(){
        for (Player p : Bukkit.getOnlinePlayers()){
            UUID u = openInventories.get(p.getUniqueId());
            if (u.equals(getUuid())){
                p.closeInventory();
            }
        }
        inventoriesByUUID.remove(getUuid());
    }

    public void addItemFromNavConfig(int slot, int i, int page, int totalPages){
        ItemStack item = new ItemStack(Material.getMaterial(Hats.getPlugin().getConfig().getString("nav." + slot + ".material")));

        if(item.getType().equals(Material.AIR)){
            setItem(i, item);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if(!Hats.getPlugin().getConfig().getString("nav." + slot + ".displayName").equals("")){
            String name = Hats.getPlugin().getConfig().getString("nav." + slot + ".displayName");
            name = ChatUtils.parseColourCodes(name);
            meta.setDisplayName(name);
        }
        if(!Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").equals("")){
            ArrayList<String> lore = new ArrayList<>();
            String[] loreList = Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").split(";");
            for(String loreString : loreList){
                loreString = loreString.replace("%page%", String.valueOf(page));
                loreString = loreString.replace("%totalPages%", String.valueOf(totalPages));
                lore.add(ChatUtils.parseColourCodes(loreString));
            }
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        if(Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").equals("null")){
            setItem(i, item);
        } else if(Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").equals("REMOVE")){
            setItem(i, item, player -> {
                Hats.getPlugin().removeFunction(player);
            });
        } else if(Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").equals("BACK")){
            setItem(i, item, player -> {
                Hats.getPlugin().backFunction(player, totalPages);
            });
        } else if(Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").equals("FORWARD")){
            setItem(i, item, player -> {
                Hats.getPlugin().nextFunction(player, totalPages);
            });
        } else if(Hats.getPlugin().getConfig().getString("nav." + slot + ".lore").equals("CLOSE")){
            setItem(i, item, player -> {
                Hats.getPlugin().closeFunction(player);
            });
        }
    }

    public static Map<UUID, BaseHatGUI> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return openInventories;
    }

    public Map<Integer, hatGUIActions> getActions() {
        return actions;
    }

    public UUID getUuid() {
        return uuid;
    }

    public interface hatGUIActions {
        void click(Player player);
    }
}
