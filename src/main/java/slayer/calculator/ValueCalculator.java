package slayer.calculator;

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

    public double calcDropValue(Drop drop, Rarity assumedRarity) throws IncompleteItemListException {
        float avgAmount = drop.getAmount();
        if(avgAmount == 0) avgAmount=1;
        double unitPrice = ((Item) drop.getItemID()).getValue();

        if(unitPrice == 0.0f)
            throw new IncompleteItemListException("The supplied item list has no information for the item in the drop.");
        double avgPrice = avgAmount*unitPrice;

        float odds;
        Rarity rarity = assumedRarity == null ? drop.getDropRates().getRarity() : assumedRarity;
        if(rarity == Rarity.OUTLIER) odds = 1/drop.getDropRates().getValue().floatValue();
        else if(rarity == Rarity.ALWAYS) odds = 1;
        else odds = defaultValues.get(rarity);

        return odds*avgPrice;
    }

    public double calcDropTableValue(DropTable dropTable, boolean bypassIgnore) throws IncompleteItemListException {
        double dropTableValue = 0;
        for(Drop drop: dropTable.getDrop()){
            if(!dropTable.isIgnored() || bypassIgnore) dropTableValue += calcDropValue(drop, dropTable.getDefaultRarity());
        }
        for(DropTable.Composite.Dtref dtRef: dropTable.getComposite().getDtref())
            dropTableValue += calcDropTableValue((DropTable) dtRef.getRef(), true);
        return dropTableValue;
    }

    public double calcDropTableValue(DropTable dropTable) throws IncompleteItemListException {
        return calcDropTableValue(dropTable, false);
    }

    public double calcTaskValue(Monster monster, int killNumber) throws IncompleteItemListException {
        float totalDropTableWorth = 0;
        for(DropTable dropTable: monster.getDropTable()){
            boolean bypass = false;
            DropTable t;
            if(dropTable.getRef() == null) t = dropTable;
            else{
                bypass = true;
                t = (DropTable) dropTable.getRef();
            }
            totalDropTableWorth += calcDropTableValue(t, bypass);
        }
        return totalDropTableWorth*killNumber;
    }
}
