package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import slayer.*;
import util.exceptions.dataMiner.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jpcmarques on 08-09-2016.
 */
public class DropTableExtractor extends DataMiner<String, Elements, Element, LinkedList<DropTable>> {
    private Document document;
    private static final String classFilter = "item-drops";
    private String monsterID;
    private ItemList itemList;

    public DropTableExtractor(String input, ItemList itemList) {
        super(input);
        this.itemList = itemList;
        monsterID = input.substring(input.lastIndexOf("/") + 1);
        logger = new IDLogger(DropTableExtractor.class, DropTableExtractor.class.getName());

    }

    @Override
    protected void init() {
        unifiedData = new LinkedList<>();
    }

    private String getDTID(int index){
        return monsterID + "DropsN" + index;
    }

    @Override
    protected void validateInput() throws InvalidInputException {
        try {
            document = Jsoup.connect(input).get();
        } catch (IOException e) {
            logger.e("caught exception during input validation: " + e.getMessage());
            throw new InvalidInputException();
        }
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        logger.i("selecting all tables from document...");
        chunkedData = document.select("table");
    }

    @Override
    protected void validateDataChunk(Element chunk, int index) throws InvalidDataChunkException {
        boolean includesClass = false;
        logger.i("checking classes of \"" + chunk.nodeName() + "\"");
        for(String cl : chunk.classNames()){
            logger.i("found class \"" + cl + "\"");
            if(classFilter.contains(cl)){
                includesClass = true;
                break;
            }
        }
        if(!includesClass){
            logger.e("\"" + chunk.nodeName() + "\" does not contain required class.");
            throw new InvalidDataChunkException("data chunk (table) does not contain the required class.");
        }
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
            System.out.println(line.toString());
            Drop drop = new Drop();
            DropRates dropRates = new DropRates();
            String initialPrice = line.child(4).text();
            String itemName = line.child(1).child(0).text();
            if(initialPrice.toLowerCase().equals("not sold")) {
                logger.i("found unsellable item, " + itemName + ", skipping.");
                continue;
            }

            String amount = line.child(2).text();
            System.out.println(amount);
            if(amount.contains("\u00a0(noted)")) amount = amount.replace("\u00a0(noted)", "");
            String rarity = line.child(3).text();
            if(rarity.contains("[")) rarity = "outlier";

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
    protected void processChunks() throws InvalidDataChunkException {
        for(int i = 0; i < chunkedData.size(); i++){
            Element chunk = chunkedData.get(i);
            try{
                validateDataChunk(chunk, i);
                processDataChunk(chunk, i);
            }catch (InvalidDataChunkException idce){
                //Skip
            }
        }
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
    }

}
