package util;

import org.apache.commons.io.IOUtils;
import slayer.Item;
import slayer.ItemList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpcmarques on 29-09-2016.
 */
public abstract class ItemValueUpdater {
    protected static final String URL = "http://runescape.wikia.com/wiki/Module:Exchange/#?action=raw";
    protected static final String priceRegex = "\\s*price\\s*=\\s*\\d+";

    public static void updateItem(Item item) {
        try{
            InputStream itemDataStream = new URL(URL.replace("#", item.getId())).openStream();
            String fetchedData = IOUtils.toString(new InputStreamReader(itemDataStream)).trim();
            Matcher matcher = Pattern.compile(priceRegex).matcher(fetchedData);
            if(!matcher.find() ){
                item.setValue(1);
                return;
            }
            int value = Integer.parseInt(fetchedData.substring(matcher.start(), matcher.end()).split("=")[1].trim());
            item.setValue(value);
        } catch (IOException ioe){
            item.setValue(1);
        }
    }

    public static void updateItemList(ItemList itemList)  {
        for(Item item : itemList.getItem()) updateItem(item);
    }
}
