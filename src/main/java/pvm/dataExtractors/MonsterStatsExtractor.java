package pvm.dataExtractors;

import org.jsoup.nodes.Element;
import slayer.Monster;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;

/**
 * Created by jpcmarques on 21-09-2016.
 */
public class MonsterStatsExtractor extends MonsterDataExtractor<Monster> {
    private static final String EXP_KEY = "experience",
            HP_EXP_KEY = "hpxp",
            NAME_KEY = "name",
            SLAYER_EXP_KEY = "slayerxp",
            SLAYER_CAT_KEY = "slayercat",
            EXAMINE_KEY = "examine";

    private static final String[] BANNED_STRINGS = new String[] {
            "<span></span>"
    };

    public MonsterStatsExtractor(String input, Monster monster) {
        super(input, "", monster);
    }

    @Override
    protected void chunkData() throws InvalidChunkingException {
        //Data pre-chunked
    }

    @Override
    protected void validateInput() throws InvalidInputException {
        //Input is pre-validated
    }

    @Override
    protected void init() {
        unifiedData = monster;
    }

    @Override
    protected void processDataChunk(Element element, int index) throws InvalidDataChunkException {
        String exp = element.getElementsByAttribute(EXP_KEY).first().text();
        String hpexp = element.getElementsByAttribute(HP_EXP_KEY).first().text();
        String name = element.getElementsByAttribute(NAME_KEY).first().text();
        String slayerexp = element.getElementsByAttribute(SLAYER_EXP_KEY).first().text();
        String slayercat = element.getElementsByAttribute(SLAYER_CAT_KEY).first().text();
        String examine = element.getElementsByAttribute(EXAMINE_KEY).first().text();

        for(String s: BANNED_STRINGS){
            slayercat = slayercat.replace(s, "");
            name = name.replace(s, "");
            examine = examine.replace(s, "");
        }

        float expf = Float.parseFloat(exp);
        float slayerexpf = Float.parseFloat(slayerexp);
        float hpexpf = Float.parseFloat(hpexp);

        unifiedData.setName(name);
        unifiedData.setArchetype(slayercat);
        unifiedData.setCombatExp(expf);
        unifiedData.setHpExp(hpexpf);
        unifiedData.setSlayerExp(slayerexpf);
        unifiedData.setExamine(examine);
    }

    @Override
    protected void validateResult() throws InvalidResultException {
        //Always valid
    }
}
