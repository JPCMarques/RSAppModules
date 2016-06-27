package slayer;

import slayer.exceptions.CorruptedItemListException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jpcmarques on 27-06-2016.
 */
public abstract class Loader {
    public static DropData loadDropData() throws JAXBException, CorruptedItemListException {
        return loadDropData(null);
    }

    public static DropData loadDropData(InputStream inputStream) throws JAXBException, CorruptedItemListException {
        DataAccessor accessor = DataAccessor.getInstance();
        DropData data = accessor.unmarshallDropData();
        if(accessor.itemListExists()){
            ItemList itemList = (inputStream == null ?
                    accessor.unmarshallItemList() :
                    accessor.unmarshallItemList(inputStream));
            DataAccessor.ItemListStatus status = accessor.getStatus(itemList, data);
            if(status.issueCount() > 0)
                throw new CorruptedItemListException("Item list is corrupted." +
                        "\n" + status.toString());
            data.setItemList(itemList);
        }
        return data;
    }

    public static void saveDropData(DropData data) throws JAXBException {
        saveDropData(data, null);
    }

    public static void saveDropData(DropData data, OutputStream outputStream) throws JAXBException {
        ItemList itemList = data.getItemList();
        DataAccessor accessor = DataAccessor.getInstance();
        if (outputStream == null) accessor.marshallItemList(itemList);
        else accessor.marshallItemList(itemList, outputStream);
    }
}
