package pvm.calculator;

import slayer.*;

import java.util.HashMap;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class ValueCalculator {
    private static HashMap<Rarity, Float> defaultValues;

    static  {
        defaultValues = new HashMap<>();
        defaultValues.put(Rarity.COMMON, 0.1f);
        defaultValues.put(Rarity.UNCOMMON, 0.01f);
        defaultValues.put(Rarity.RARE, 0.001f);
        defaultValues.put(Rarity.VERY_RARE, 0.0001f);
    }

    public static double calcDropValue(Drop drop, Rarity assumedRarity) {
        float avgAmount = drop.getAmount();
        if(avgAmount == 0) avgAmount=1;
        double unitPrice = ((Item) drop.getItemID()).getValue();

        double avgPrice = avgAmount*unitPrice;

        float odds;
        Rarity rarity = assumedRarity == null ? drop.getDropRates().getRarity() : assumedRarity;
        if(rarity == Rarity.OUTLIER) odds = 1/drop.getDropRates().getValue().floatValue();
        else if(rarity == Rarity.ALWAYS) odds = 1;
        else odds = defaultValues.get(rarity);

        return odds*avgPrice;
    }

    public static double calcDropTableValue(DropTable dropTable, boolean bypassIgnore) {
        double dropTableValue = 0;
        for(Drop drop: dropTable.getDrop()){
            dropTableValue += calcDropValue(drop, drop.getDropRates().getRarity());
        }
        return dropTableValue;
    }

    public static double calcDropTableValue(DropTable dropTable)  {
        return calcDropTableValue(dropTable, false);
    }

    public static double calcTaskValue(Monster monster, int killNumber)  {
        float totalDropTableWorth = (float) calcDropTableValue(monster.getDropTable());
        return totalDropTableWorth*killNumber;
    }
}
