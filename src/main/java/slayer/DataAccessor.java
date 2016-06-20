package slayer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jpcmarques on 12-06-2016.
 */
public class DataAccessor {
    private static final String DROP_DATA_RES_LOC = "slayer/monsterDropData.xml";
    private static final String ITEM_LIST_LOC = "itemList.xml";
    private static DataAccessor instance;

    public static DataAccessor getInstance(){
        if(instance == null) instance = new DataAccessor();
        return instance;
    }

    public DropData unmarshallDropData() throws JAXBException {
        ClassLoader classLoader = getClass().getClassLoader();
        File dropDataFile = new File(classLoader.getResource(DROP_DATA_RES_LOC).getFile());

        JAXBContext jaxbContext = JAXBContext.newInstance(DropData.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return  (DropData) unmarshaller.unmarshal(dropDataFile);
    }

    public ItemList unmarshallItemList() throws JAXBException {
        File itemListFile = new File(ITEM_LIST_LOC);

        JAXBContext jaxbContext = JAXBContext.newInstance(ItemList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return  (ItemList) unmarshaller.unmarshal(itemListFile);
    }

    public void marshallItemList(ItemList itemList) throws JAXBException {
        File itemListFile = new File(ITEM_LIST_LOC);
        JAXBContext jaxbContext = JAXBContext.newInstance(ItemList.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.marshal(itemList, itemListFile);
    }

    public boolean itemListExists(){
        return new File(ITEM_LIST_LOC).exists();
    }

    public static class ItemListStatus{
        public int missingItems, additionalItems;
    }

    public ItemListStatus getStatus(ItemList itemList, DropData data){
        ItemList dataItemList = data.getItemList();
        HashMap<Integer, String> dataItemListInfo = new HashMap<>();
        for(Item i: dataItemList.getItem())
            dataItemListInfo.put(i.getRsid().intValue(), i.getId());
        ItemListStatus status = new ItemListStatus();

        for(Item i: itemList.getItem()){
            int rsID = i.getRsid().intValue();
            if(!dataItemListInfo.containsKey(rsID)
                    || !dataItemListInfo.get(rsID).equals(i.getId())){
                status.additionalItems++;
                dataItemListInfo.remove(rsID);
            }
        }
        status.missingItems = dataItemListInfo.keySet().size();
        return status;
    }
}
