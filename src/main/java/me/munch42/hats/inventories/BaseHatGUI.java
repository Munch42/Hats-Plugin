package me.munch42.hats.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
