package pvm;

import org.apache.commons.io.IOUtils;
import slayer.Item;
import slayer.ItemList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;

/**
 * Created by jpcmarques on 29-09-2016.
 */
public abstract class ItemDataUpdater {
    protected static final String URL = "http://runescape.wikia.com/wiki/Module:Exchange/#?action=raw";

    public static void updateItem(Item item) throws IOException {
        String url = URL.replace("#", item.getId());
        try {
            InputStream in = new URL(url).openStream();
            String data = IOUtils.toString(new InputStreamReader(in));
            IOUtils.closeQuietly(in);

            String[] lines = data.split("\n");

            String itemID = lines[1].split("=")[1].trim();
            String itemPrice = lines[2].split("=")[1].trim();
            itemID = itemID.substring(0, itemID.length() - 1);
            itemPrice = itemPrice.substring(0, itemPrice.length() - 1);

            item.setRsid(BigInteger.valueOf(Long.parseLong(itemID)));
            item.setValue(Double.parseDouble(itemPrice));
        } catch (FileNotFoundException fnfe){
            item.setRsid(BigInteger.valueOf(0));
        }
    }

    public static void updateItemList(ItemList itemList) throws IOException {
        for(Item item : itemList.getItem()) updateItem(item);
    }
}
