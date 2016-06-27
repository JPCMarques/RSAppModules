package calculator;

import slayer.*;
import slayer.exceptions.IncompleteItemListException;

import java.util.HashMap;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class ValueCalculator {
    private HashMap<Rarity, Float> defaultValues;

    public ValueCalculator() {
        defaultValues = new HashMap<>();
        defaultValues.put(Rarity.COMMON, 0.1f);
        defaultValues.put(Rarity.UNCOMMON, 0.01f);
        defaultValues.put(Rarity.RARE, 0.001f);
        defaultValues.put(Rarity.VERY_RARE, 0.0001f);
    }

    public double calcDropValue(ItemList list, Drop drop) throws IncompleteItemListException {
        float avgAmount = drop.getAmount();
        double unitPrice = ((Item) drop.getItemID()).getValue();

        if(list != null){
            for(Item i : list.getItem()){
                if(i.equals(drop.getItemID())) {
                    unitPrice = i.getValue();
                    break;
                }
            }
        }

        if(unitPrice == 0.0f)
            throw new IncompleteItemListException("The supplied item list has no information for the item in the drop.");
        double avgPrice = avgAmount*unitPrice;

        float odds;
        Rarity rarity = drop.getDropRates().getRarity();
        if(rarity == Rarity.OUTLIER) odds = 1/drop.getDropRates().getValue().floatValue();
        else if(rarity == Rarity.ALWAYS) odds = 1;
        else odds = defaultValues.get(rarity);

        return odds*avgPrice;
    }

    public double calcDropTableValue(ItemList list, DropTable dropTable) throws IncompleteItemListException {
        double dropTableValue = 0;
        for(Drop drop: dropTable.getDrop()) dropTableValue += calcDropValue(list, drop);
        return dropTableValue;
    }

    public double calcTaskValue(ItemList list, Monster monster, int killNumber) throws IncompleteItemListException {
        float totalDropTableWorth = 0;
        for(DropTable dropTable: monster.getDropTable()) totalDropTableWorth += calcDropTableValue(list, dropTable);
        return totalDropTableWorth*killNumber;
    }

    public double calcTaskValue(Monster monster, int killNumber) throws IncompleteItemListException {
        return calcTaskValue(null, monster, killNumber);
    }

    public double calcDropTableValue(DropTable dropTable) throws IncompleteItemListException {
        return calcDropTableValue(null, dropTable);
    }

    public double calcDropValue(Drop drop) throws IncompleteItemListException {
        return calcDropValue(null, drop);
    }
}
