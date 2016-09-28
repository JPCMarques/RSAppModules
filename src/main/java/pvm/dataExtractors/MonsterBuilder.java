package pvm.dataExtractors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import slayer.DropTable;
import slayer.ItemList;
import slayer.Monster;
import util.DataExtractor;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;

import java.util.LinkedList;

/**
 * Created by jpcmarques on 21-09-2016.
 */
public class MonsterBuilder extends RSWikiTableExtractor<LinkedList<Monster>> {
    protected ItemList itemList;

    public MonsterBuilder(String input, ItemList itemList) {
        super("http://runescape.wikia.com/wiki/" + input.replace(" ", "_"), "infobox-monster");
        this.itemList = itemList;
    }

    @Override
    protected void init() {
        unifiedData = new LinkedList<>();
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        try{
            super.chunkData();
        }catch (Exception ex){
            Elements boxedData = document.getElementsByClass("switch-infobox").first().children();
            boxedData.remove(0);
            boxedData.remove(0);
            chunkedData.addAll(boxedData);
        }
    }

    @Override
    protected void processDataChunk(Element element, int index) throws InvalidDataChunkException, InvalidResultException, InvalidInputException, InvalidChunkingException {
        Monster monster = new Monster();

        String name = input.substring(input.lastIndexOf("/") + 1);

        monster.setMonsterID(name);
        monster.setName(name.replace("_", " "));

        CharmTableExtractor cte = new CharmTableExtractor(input, monster);
        DropTableExtractor dte = new DropTableExtractor(input, monster, itemList);
        MasterListExtractor mle = new MasterListExtractor(element.toString(), monster);
        MonsterStatsExtractor mse = new MonsterStatsExtractor(element.toString(), monster);
        mse.mine();

        LinkedList<DropTable> dropTables = dte.mine();
        Monster.CharmList charmList = cte.mine();
        Monster.MasterList masterList = mle.mine();

        monster.setCharmList(charmList);
        monster.getDropTable().addAll(dropTables);
        monster.setMasterList(masterList);

        unifiedData.add(monster);
    }

    @Override
    protected void validateResult() throws InvalidResultException {

    }
}
