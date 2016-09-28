package pvm.dataExtractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import slayer.Monster;
import slayer.SlayerMaster;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;

import java.util.HashMap;

/**
 * Created by jpcmarques on 11-09-2016.
 */
public class MasterListExtractor extends MonsterDataExtractor<Monster.MasterList> {
    private String attributeToSearch = "assigned_by",
            BASE_KEY = "data-attr-param";
    private static final String FILTER = ""; //Everything is accepted; assumed input directly from processed monster data.
    private HashMap<String, SlayerMaster> masterList;

    public MasterListExtractor(String input, Monster monster) {
        super(input, FILTER, monster);
    }

    @Override
    protected void validateInput() throws InvalidInputException {
        document = Jsoup.parse(input, "", Parser.xmlParser());
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        chunkedData = document.getElementsByAttributeValue(BASE_KEY, attributeToSearch);
        if (chunkedData.size() == 0) chunkedData = document.getElementsByClass("item");

    }

    @Override
    protected void init() {
        unifiedData = new Monster.MasterList();
        masterList = new HashMap<>();
        for(SlayerMaster master : SlayerMaster.values()){
            String masterName = master.value();
            if(masterName.contains("/")) masterName = masterName.split("/")[0];
            masterList.put(masterName, master);
            System.out.println(masterName);
        }
    }

    @Override
    protected void processDataChunk(Element element, int index) throws InvalidDataChunkException {
        System.out.println("FUCKING MASTERLIST " + index + " FOR\n" + element);
        for(String masterName : masterList.keySet()){
            if(element.toString().toLowerCase().contains(masterName.toLowerCase())){
                unifiedData.getMaster().add(masterList.get(masterName));

            }
        }

        //TODO check "old-style" wiki pages
    }

    @Override
    protected void validateResult() throws InvalidResultException {
        //No validation needed
    }
}
