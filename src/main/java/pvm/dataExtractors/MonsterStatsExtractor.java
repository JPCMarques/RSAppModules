package pvm.dataExtractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import slayer.Monster;
import util.converters.NumberConverter;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;

/**
 * Created by jpcmarques on 21-09-2016.
 */
public class MonsterStatsExtractor extends MonsterDataExtractor<Void> {
    private static final String EXP_KEY = "experience",
            HP_EXP_KEY = "hpxp",
            NAME_KEY = "name",
            SLAYER_EXP_KEY = "slayxp",
            SLAYER_CAT_KEY = "slayercat",
            EXAMINE_KEY = "examine",
            BASE_KEY = "data-attr-param";

    private static final String[] BANNED_STRINGS = new String[] {

    };

    public MonsterStatsExtractor(String input, Monster monster) {
        super(input, "", monster);
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        chunkedData = new Elements();
        chunkedData.add(document);
    }

    @Override
    protected void validateInput() throws InvalidInputException {
        document = Jsoup.parse(input, "", Parser.xmlParser());
    }

    @Override
    protected void init() {
        //No init steps required.
    }

    @Override
    protected void validateResult() throws InvalidResultException {
        //Always valid
    }

    private void removeBannedStrings(String src){
        for(String s: BANNED_STRINGS) src = src.replace(s, "");
    }

    @Override
    protected void processDataChunk(Element element, int index) throws InvalidDataChunkException {
        String name = element.getElementsByAttributeValue(BASE_KEY, NAME_KEY).first().text(),
                exp = element.getElementsByAttributeValue(BASE_KEY, EXP_KEY).first().text(),
                hpExp = element.getElementsByAttributeValue(BASE_KEY, HP_EXP_KEY).first().text(),
                examine = element.getElementsByAttributeValue(BASE_KEY, EXAMINE_KEY).first().text(),
                slayerExp = element.getElementsByAttributeValue(BASE_KEY, SLAYER_EXP_KEY).first().text(),
                slayerCat = element.getElementsByAttributeValue(BASE_KEY, SLAYER_CAT_KEY).first().text();

        removeBannedStrings(name);
        removeBannedStrings(slayerCat);
        removeBannedStrings(examine);

        monster.setName(name);
        monster.setMonsterID(name.replace("\u0020", "_"));
        monster.setCombatExp((float) NumberConverter.toDouble(exp));
        monster.setHpExp((float) NumberConverter.toDouble(hpExp));
        monster.setSlayerExp((float) NumberConverter.toDouble(slayerExp));
        monster.setExamine(examine);
        monster.setArchetype(slayerCat);
    }
}
