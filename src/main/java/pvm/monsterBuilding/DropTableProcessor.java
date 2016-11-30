package pvm.monsterBuilding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.ItemValueUpdater;
import slayer.*;
import util.converters.NumberConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joaop on 26/10/2016.
 */
public class DropTableProcessor extends StringProcessor<DropTable> {
    protected ItemList itemList;
    protected HashMap<String, Item> itemMap;
    protected String start = "{{DropsLine|", end = "}}\n";
    protected static final String URL_START = "http://runescape.wikia.com/wiki/Module:Exchange/", URL_END = "?action=raw";
    protected Pattern pattern = Pattern.compile("\\{\\{DropsLine\\|.*\\}\\}\n");
    protected Logger logger = LogManager.getLogger();
    protected HashMap<String, Float> foundNotes = new HashMap<>();

    public DropTableProcessor(DropTable dropTable, String input, ItemList itemList) {
        super(dropTable, input);
        this.itemList = itemList;

    }

    @Override
    public void process() throws IOException {
        itemMap = new HashMap<>();
        for(Item item : itemList.getItem()){
            itemMap.put(item.getId(), item);
        }

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()){
            Item item = new Item();
            String cleanData = input.substring(matcher.start() + start.length(), matcher.end() - end.length());
            String[] splitData = cleanData.split("\\|");
            HashMap<String, String> tokenVals = new HashMap<>();
            for(String col : splitData){
                logger.debug("col {}", col);
                String[] splitCol = col.toLowerCase().split("=");
                try{
                    tokenVals.put(splitCol[0], splitCol[1].trim());
                }catch (ArrayIndexOutOfBoundsException e){
                    logger.debug("Index out of bounds found, continuing");
                }
            }
            String name = tokenVals.get("name");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            String iid = name.replace(" ", "_");
            if(name.equals("Rare_drop_table")) continue;
            boolean toAdd = itemMap.get(iid) == null;

            float value;
            String quantityData = tokenVals.get("quantity").replace(" (noted)", "");
            logger.debug("Quantity data: {}" , quantityData);
            float quantity = (float) NumberConverter.toDouble(quantityData);
            String rarity = tokenVals.get("rarity").toLowerCase();
            logger.debug("Rarity: {}", rarity);
            Rarity rarityEnum = (rarity.equals("varies") ? Rarity.VERY_RARE : Rarity.fromValue(rarity));


            float altRarity = 0;
            if (cleanData.toLowerCase().contains("raritynotes")){
                logger.debug("Processing alternative rarities...");
                altRarity = fetchAltRarity(cleanData);
                if(altRarity != 0) rarityEnum = Rarity.OUTLIER;
                logger.debug("Done. Found alt rarity {}", altRarity);
            }

            item.setName(name);
            item.setId(iid);

            logger.debug("Processing item value data...");
            ItemValueUpdater.updateItem(item);
            logger.debug("Done.");


            if(toAdd) itemList.getItem().add(item);

            Drop drop = new Drop();
            drop.setItemID(item);
            drop.setAmount(quantity);
            DropRates dropRates = new DropRates();
            dropRates.setRarity(rarityEnum);
            if(rarityEnum == Rarity.OUTLIER) {
                if(altRarity != 0) dropRates.setValue((int) altRarity);

            }
            drop.setDropRates(dropRates);
            target.getDrop().add(drop);
        }

        logger.debug("Notes found:");
        for (String key : foundNotes.keySet()) logger.debug("{} : {}", key, foundNotes.get(key));

    }



    private float fetchAltRarity(String data){
        logger.debug("Alt rarity data: {}", data);
        Pattern rootPattern = Pattern.compile("name\\s*=\\s*(\")?\\w*(\")?");
        Matcher rootMatcher = rootPattern.matcher(data);
        boolean dataExists = rootMatcher.find();
        String name = null;
        if(dataExists){
            String rootData = data.substring(rootMatcher.start(), rootMatcher.end());
            logger.debug("Root data: {}", rootData);
            if (!rootData.contains("\"")) dataExists = false;
            name = (dataExists ? rootData.substring(rootData.indexOf("\"") + 1,
                    rootData.lastIndexOf("\"")) :
                    rootData.substring(rootData.indexOf("=") + 1));


            if(dataExists) {
                if(rootData.contains("/")) return foundNotes.get(name);
                return 0;
            }
        }

        Pattern pattern = Pattern.compile("\\d+/\\d+");
        Matcher matcher = pattern.matcher(data);
        if(!matcher.find()) return 0;
        logger.debug("Matched pattern: {}", data.substring(matcher.start(), matcher.end()));
        String matchedData = data.substring(matcher.start(), matcher.end());
        String[] splitData = matchedData.split("/");
        float rarity = Float.parseFloat(splitData[1]);

        if(name != null) foundNotes.put(name, rarity);
        return rarity;
    }
}
