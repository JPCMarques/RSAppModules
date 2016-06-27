package slayer;

import slayer.exceptions.CorruptedItemListException;

import javax.xml.bind.JAXBException;
import java.io.File;

/**
 * Created by jpcmarques on 27-06-2016.
 */
public abstract class Loader {
    public static DropData loadDropData() throws JAXBException, CorruptedItemListException {
        DataAccessor accessor = DataAccessor.getInstance();
        DropData data = accessor.unmarshallDropData();
        if(accessor.itemListExists()){
            ItemList itemList = accessor.unmarshallItemList();
            DataAccessor.ItemListStatus status = accessor.getStatus(itemList, data);
            if(status.issueCount() > 0)
                throw new CorruptedItemListException("Item list is corrupted." +
                        "\n" + status.toString());
            data.setItemList(itemList);
        }
        return data;
    }

    public static void saveDropData(DropData data) throws JAXBException {
        ItemList itemList = data.getItemList();
        DataAccessor.getInstance().marshallItemList(itemList);
    }
}
