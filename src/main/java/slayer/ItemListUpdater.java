package slayer;

import converters.GPConverter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jpcmarques on 27-06-2016.
 */
public abstract class ItemListUpdater {
    private static final String itemQuery = "http://services.runescape.com/m=itemdb_rs/api/catalogue/detail.json?item=";
    private static final String itemKey = "item", targetDateKey = "current", priceKey = "price";

    public static double getItemPrice(Item item) throws IOException {
        int rsID = item.getRsid().intValue();
        if(rsID == 0) return 1;
        String priceLookup = itemQuery + rsID;

        URL geAPI = new URL(priceLookup);
        URLConnection yc = geAPI.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        String inputLine = in.readLine();
        in.close();

        JSONObject data = new JSONObject(inputLine);
        String rsPrice = data.getJSONObject(itemKey).getJSONObject(targetDateKey).get(priceKey).toString();
        return GPConverter.convert(rsPrice);
    }

    public static void updateMonsterDropValue(Monster data) throws IOException {
        for(DropTable dropTable : data.getDropTable()){
            for(Drop drops: dropTable.getDrop()){
                Item item = (Item) drops.getItemID();
                item.setValue(getItemPrice(item));
            }
        }
    }
}
