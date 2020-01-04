package me.munch42.hats.inventories;

public class HatGUI extends BaseHatGUI {
    public HatGUI(int invSize, int pageNum, int totalPages, String title){
        super(invSize, title);

        for(int i = invSize - 9; i < invSize; i++){
            if(i == invSize - 9){
                addItemFromNavConfig(1, i, pageNum, totalPages);
            } else if(i == invSize - 8){
                addItemFromNavConfig(2, i, pageNum, totalPages);
            } else if(i == invSize - 7){
                addItemFromNavConfig(3, i, pageNum, totalPages);
            } else if (i == invSize - 6){
                addItemFromNavConfig(4, i, pageNum, totalPages);
            } else if(i == invSize - 5){
                addItemFromNavConfig(5, i, pageNum, totalPages);
            } else if(i == invSize - 4){
                addItemFromNavConfig(6, i, pageNum, totalPages);
            } else if(i == invSize - 3){
                addItemFromNavConfig(7, i, pageNum, totalPages);
            } else if(i == invSize - 2){
                addItemFromNavConfig(8, i, pageNum, totalPages);
            } else if(i == invSize - 1){
                addItemFromNavConfig(9, i, pageNum, totalPages);
            }

        }
    }

}
