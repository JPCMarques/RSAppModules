package pvm.dataExtractors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import slayer.Charm;
import slayer.CharmDropRate;
import slayer.Monster;
import util.converters.NumberConverter;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidResultException;

import java.util.List;

/**
 * Created by jpcmarques on 10-09-2016.
 */
public class CharmTableExtractor extends MonsterDataExtractor<Monster.CharmList> {

    public CharmTableExtractor(String s, Monster monster) {
        super(s, "charmtable", monster);
    }

    @Override
    protected void init() {
        unifiedData = new Monster.CharmList();
    }

    @Override
    protected void processDataChunk(Element element, int index) throws InvalidDataChunkException {
        Elements percentages = element.child(1).children();
        logger.i("getting all child nodes...");
        List<CharmDropRate> charmDropRates = unifiedData.getCharmDropRate();
        Charm[] charms = new Charm[]{
                Charm.GOLD,
                Charm.GREEN,
                Charm.CRIMSON,
                Charm.BLUE
        };
        logger.i("initiating data processing...");
        for(int i = 1; i < percentages.size(); i++){
            String data = percentages.get(i).text();
            data = data.substring(0, data.indexOf("%"));
            double avg = NumberConverter.singleRangeConversion(data);
            CharmDropRate charmDropRate = new CharmDropRate();
            charmDropRate.setBase(charms[i]);
            charmDropRate.setRate((float) avg);
            charmDropRates.add(charmDropRate);
        }
        unifiedData.setId(monster.getMonsterID() + "_charms");
    }

    @Override
    protected void validateResult() throws InvalidResultException {

    }
}
