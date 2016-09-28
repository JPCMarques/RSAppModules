package pvm.dataExtractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import slayer.Monster;
import util.ElementDigger;
import util.converters.NumberConverter;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;
import util.html.tags.TableRowHD;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Created by jpcmarques on 21-09-2016.
 */
public class MonsterStatsExtractor extends MonsterDataExtractor<Void> {
    private float lastCbExp, lastHpExp;
    private static final String EXP_KEY = "experience",
            HP_EXP_KEY = "hpxp",
            LEVEL_KEY = "level",
            SLAYER_EXP_KEY = "slayxp",
            SLAYER_CAT_KEY = "slayercat",
            EXAMINE_KEY = "examine",
            BASE_KEY = "data-attr-param";

    private enum OldKeys{
        EXAMINE(0,0,0,1,5,1),
        SLAYER_EXP(0,0,0,1,8,1),
        EXP(0,0,0,1,4,1),
        LEVEL(0,0,0,1,3,1)
        ;

        OldKeys(int...  sequence) {
            this.sequence = sequence;
        }

        int[] sequence;
    }


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

    @Override
    protected void processDataChunk(Element element, int index) throws InvalidDataChunkException {
        try {
            String exp = element.getElementsByAttributeValue(BASE_KEY, EXP_KEY).first().text(),
                    hpExp = element.getElementsByAttributeValue(BASE_KEY, HP_EXP_KEY).first().text(),
                    examine = element.getElementsByAttributeValue(BASE_KEY, EXAMINE_KEY).first().text(),
                    slayerExp = element.getElementsByAttributeValue(BASE_KEY, SLAYER_EXP_KEY).first().text(),
                    slayerCat = element.getElementsByAttributeValue(BASE_KEY, SLAYER_CAT_KEY).first().text(),
                    level = element.getElementsByAttributeValue(BASE_KEY, LEVEL_KEY).first().text();

            monster.setCombatExp((float) NumberConverter.toDouble(exp));
            monster.setHpExp((float) NumberConverter.toDouble(hpExp));
            monster.setSlayerExp((float) NumberConverter.toDouble(slayerExp));
            monster.setMonsterID(monster.getMonsterID() + "_" + level);
            monster.setExamine(examine);
            monster.setArchetype(slayerCat);
        } catch (NullPointerException ex){
            String examineData = ElementDigger.dig(element, OldKeys.EXAMINE.sequence).text();
            String expData = ElementDigger.dig(element, true, OldKeys.EXP.sequence).text();
            String levelData = ElementDigger.dig(element, OldKeys.LEVEL.sequence).text();
            String slayerExpData = ElementDigger.dig(element, OldKeys.SLAYER_EXP.sequence).text();
            String trimmedExpData = expData.trim();
            String cbExpData, hpExpData;
            if(trimmedExpData.length() == 0){
                cbExpData = lastCbExp + "";
                hpExpData = lastHpExp + "";
            } else {
                String[] expDataTokens = trimmedExpData.split(" ");
                cbExpData = expDataTokens[0];
                hpExpData = expDataTokens[2];
            }

            monster.setExamine(examineData);
            monster.setArchetype(monster.getName());
            monster.setMonsterID(monster.getMonsterID() + "_" + levelData);
            monster.setSlayerExp((float) NumberConverter.toDouble(slayerExpData.trim()));
            lastHpExp = (float) NumberConverter.toDouble(hpExpData);
            monster.setHpExp(lastHpExp);
            lastCbExp = (float) NumberConverter.toDouble(cbExpData);
            monster.setCombatExp(lastCbExp);

        }
    }
}
