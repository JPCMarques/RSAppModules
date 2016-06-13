package slayer;

import slayer.exceptions.IncompleteItemListException;

import java.util.HashMap;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class ValueCalculator {
    public static final float COMMON = 0.1f, UNCOMMON = 0.01f, RARE=0.001f, VERY_RARE = 0.0001f;
    private static HashMap<Rarity, Float> defaultValues;

    static{
        defaultValues = new HashMap<>();
        defaultValues.put(Rarity.COMMON, 0.1f);
        defaultValues.put(Rarity.UNCOMMON, 0.01f);
        defaultValues.put(Rarity.RARE, 0.001f);
        defaultValues.put(Rarity.VERY_RARE, 0.0001f);
    }

    public static float calcDropValue(ItemList list, Drop drop) throws IncompleteItemListException {
        float avgAmount = drop.getAmount();
        float unitPrice = ((Item) drop.getItemID()).getValue();
        if(unitPrice == 0.0f)
            throw new IncompleteItemListException("The supplied item list has no information for the item in the drop.");
        float avgPrice = avgAmount*unitPrice;

        float odds;
        Rarity rarity = drop.getDropRates().getRarity();
        if(rarity!=Rarity.OUTLIER) odds = defaultValues.get(rarity);
        else odds = 1/drop.getDropRates().getValue().floatValue();

        return odds*avgPrice;
    }

    public static float calcDropTableValue(ItemList list, DropTable dropTable) throws IncompleteItemListException {
        float dropTableValue = 0;
        for(Drop drop: dropTable.getDrop()) dropTableValue += calcDropValue(list, drop);
        return dropTableValue;
    }

    public static float calcTaskValue(ItemList list, Monster monster, int taskSize) throws IncompleteItemListException {
        float totalDropTableWorth = 0;
        for(DropTable dropTable: monster.getDropTable()) totalDropTableWorth += calcDropTableValue(list, dropTable);
        return totalDropTableWorth*taskSize;
    }
}
