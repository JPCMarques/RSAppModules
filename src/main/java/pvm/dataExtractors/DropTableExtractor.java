package pvm.dataExtractors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import slayer.*;
import util.IDLogger;
import util.converters.NumberConverter;
import util.exceptions.dataMiner.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jpcmarques on 08-09-2016.
 */
public class DropTableExtractor extends MonsterDataExtractor<LinkedList<DropTable>> {
    private String monsterID;
    private ItemList itemList;

    public DropTableExtractor(String input, Monster monster, ItemList itemList) {
        super(input, "item-drops", monster);
        this.itemList = itemList;
    }

    @Override
    protected void init() {
        monsterID = monster.getMonsterID();
        logger = new IDLogger(DropTableExtractor.class);
        unifiedData = new LinkedList<>();
    }

    private String getDTID(int index){
        return monsterID + "_drops_n" + index;
    }

    @Override
    protected void processDataChunk(Element chunk, int index) throws InvalidDataChunkException {
        Elements lines = chunk.child(0).children();
        DropTable dropTable = new DropTable();
        List<Drop> drops = dropTable.getDrop();
        dropTable.setId(getDTID(index));
        logger.i("processing " + getDTID(index));

        for(int i = 1; i < lines.size(); i++){
            Element line = lines.get(i);
            logger.i("line:\n"+line);
            Drop drop = new Drop();
            DropRates dropRates = new DropRates();
            String initialPrice = line.child(4).text();
            String itemName = line.child(1).child(0).text();
            if(initialPrice.toLowerCase().equals("not sold")) {
                logger.i("found unsellable item, " + itemName + ", skipping.");
                continue;
            }

            String amount = line.child(2).text();
            if(amount.contains("\u00a0(noted)")) amount = amount.replace("\u00a0(noted)", "");
            String rarity = line.child(3).text();
            if(rarity.contains("[")) rarity = "Outlier";

            logger.i("Found item " + itemName
                    + " x" + amount
                    + " with value " + initialPrice
                    + " and rarity " + rarity);

            Rarity actualRarity = Rarity.fromValue(rarity.toLowerCase());
            dropRates.setRarity(actualRarity);
            if(actualRarity.equals(Rarity.OUTLIER)) dropRates.setValue(BigInteger.valueOf(-1));

            Item item = new Item();
            item.setValue(NumberConverter.toDouble(initialPrice));
            item.setId(itemName.replace(" ", "_"));
            item.setRsid(BigInteger.valueOf(-1));
            item.setName(itemName);

            drop.setItemID(item);
            drop.setAmount((float) NumberConverter.toDouble(amount));
            drop.setDropRates(dropRates);

            logger.i("drop added to droptable.");
            drops.add(drop);
        }
        unifiedData.add(dropTable);
    }

    @Override
    protected void validateResult() throws InvalidResultException {
        List<Item> items = itemList.getItem();
        HashMap<String, Drop> droptableItems = new HashMap<>();
        for(DropTable dt : unifiedData){
            for(Drop drop : dt.getDrop()){
                Item item = (Item) drop.getItemID();
                if(droptableItems.containsKey(item.getId()))
                    drop.setItemID(droptableItems.get(item.getId()).getItemID());
                else droptableItems.put(item.getId(), drop);
            }
        }
        for(Item item: items){
            if(droptableItems.containsKey(item.getId())){
                droptableItems.get(item.getId()).setItemID(item);
                droptableItems.remove(item.getId());
            }
        }
        for(String key : droptableItems.keySet()){
            items.add((Item) droptableItems.get(key).getItemID());
        }
        for(int i = unifiedData.size() - 1; i >= 0; i--){
            DropTable dt = unifiedData.get(i);
            if(dt.getDrop().size() == 0) unifiedData.remove(i);
        }

    }

}
