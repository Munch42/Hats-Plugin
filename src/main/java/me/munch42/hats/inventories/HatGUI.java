package me.munch42.hats.inventories;

import me.munch42.hats.Hats;
import org.bukkit.inventory.ItemStack;

public class HatGUI extends BaseHatGUI {
    public HatGUI(int invSize, int pageNum, int totalPages, String title){
        super(invSize, title);

        for(int i = invSize - 9; i < invSize; i++){
            if(i == invSize - 9){
                ItemStack item = getItemFromNavConfig(1, pageNum, totalPages);
                jobCheck(1, i, item, totalPages);
            } else if(i == invSize - 8){
                ItemStack item = getItemFromNavConfig(2, pageNum, totalPages);
                jobCheck(2, i, item, totalPages);
            } else if(i == invSize - 7){
                ItemStack item = getItemFromNavConfig(3, pageNum, totalPages);
                jobCheck(3, i, item, totalPages);
            } else if (i == invSize - 6){
                ItemStack item = getItemFromNavConfig(4, pageNum, totalPages);
                jobCheck(4, i, item, totalPages);
            } else if(i == invSize - 5){
                ItemStack item = getItemFromNavConfig(5, pageNum, totalPages);
                jobCheck(5, i, item, totalPages);
            } else if(i == invSize - 4){
                ItemStack item = getItemFromNavConfig(6, pageNum, totalPages);
                jobCheck(6, i, item, totalPages);
            } else if(i == invSize - 3){
                ItemStack item = getItemFromNavConfig(7, pageNum, totalPages);
                jobCheck(7, i, item, totalPages);
            } else if(i == invSize - 2){
                ItemStack item = getItemFromNavConfig(8, pageNum, totalPages);
                jobCheck(8, i, item, totalPages);
            } else if(i == invSize - 1){
                ItemStack item = getItemFromNavConfig(9, pageNum, totalPages);
                jobCheck(9, i, item, totalPages);
            }

        }
    }

    private void jobCheck(int slot, int i, ItemStack item, int totalPages){
        switch (Hats.getPlugin().getConfig().getString("nav." + slot + ".job")) {
            case "":
                setItem(i, item);
                break;
            case "REMOVE":
                setItem(i, item, player -> {
                    Hats.getPlugin().removeFunction(player);
                });
                break;
            case "BACK":
                setItem(i, item, player -> {
                    Hats.getPlugin().backFunction(player, totalPages);
                });
                break;
            case "FORWARD":
                setItem(i, item, player -> {
                    Hats.getPlugin().nextFunction(player, totalPages);
                });
                break;
            case "CLOSE":
                setItem(i, item, player -> {
                    Hats.getPlugin().closeFunction(player);
                });
                break;
        }
    }

}
