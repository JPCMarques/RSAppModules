package pvm.dataExtractors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import slayer.ArchetypeMonsterList;
import slayer.DropData;
import slayer.Monster;
import util.DataAccessor;
import util.ElementDigger;
import util.converters.NumberConverter;
import util.exceptions.dataMiner.InvalidChunkingException;
import util.exceptions.dataMiner.InvalidDataChunkException;
import util.exceptions.dataMiner.InvalidInputException;
import util.exceptions.dataMiner.InvalidResultException;

import javax.xml.bind.JAXBException;

/**
 * Created by jpcmarques on 21-09-2016.
 */
public class MonsterStatsExtractor extends MonsterDataExtractor<Void> {
    private float lastCbExp, lastHpExp, lastSlayerExp;
    private static final String EXP_KEY = "experience",
            HP_EXP_KEY = "hpxp",
            LEVEL_KEY = "level",
            SLAYER_EXP_KEY = "slayxp",
            SLAYER_CAT_KEY = "slayercat",
            EXAMINE_KEY = "examine",
            BASE_KEY = "data-attr-param";
    private DropData dropData;

    private enum DiggerKeys {
        EXAMINE(0,0,0,1,5,1),
        SLAYER_EXP(0,0,0,1,8,1),
        EXP(0,0,0,1,4,1),
        LEVEL(0,0,0,1,3,1),
        OLD_EXAMINE(0,1,4),
        OLD_LEVEL(0,1,7,0),
        OLD_CB_EXP(0,1,7,2),
        OLD_HP_EXP(0,1,7,3),
        OLD_SLAYER_EXP(0,1,14,1)
        ;

        DiggerKeys(int...  sequence) {
            this.sequence = sequence;
        }

        int[] sequence;
    }


    public MonsterStatsExtractor(String input, Monster monster, DropData dropData) {
        super(input, "", monster);
        this.dropData = dropData;
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
            modeAParsing(element);
        } catch (NullPointerException ex){
            try {
                modeBParsing(element);
            } catch (IndexOutOfBoundsException ioobe){
                modeCParsing(element);
            }
        }

        ArchetypeMonsterList archetypeMonsterList = dropData.getArchetypeMonsterList();
        if(archetypeMonsterList == null) {
            archetypeMonsterList = new ArchetypeMonsterList();
            dropData.setArchetypeMonsterList(archetypeMonsterList);
        }
        String archetype = ((String) monster.getSlayercat()).trim();
        boolean archetypeExists = false;

        for(ArchetypeMonsterList.Archetype arch : archetypeMonsterList.getArchetype()){
            if(arch.getArchetypeID().equals(archetype)){
                archetypeExists = true;
                fillArchetype(arch);
                break;
            }
        }
        if(!archetypeExists){
            ArchetypeMonsterList.Archetype arch = new ArchetypeMonsterList.Archetype();
            arch.setArchetypeID(archetype);
            fillArchetype(arch);
            archetypeMonsterList.getArchetype().add(arch);
        }
    }

    private void fillArchetype(ArchetypeMonsterList.Archetype arch){
        ArchetypeMonsterList.Archetype.ArchetypeMonster archetypeMonster = new ArchetypeMonsterList.Archetype.ArchetypeMonster();
        archetypeMonster.setMonsterID(monster);
        monster.setSlayercat(arch);
        arch.getArchetypeMonster().add(archetypeMonster);
    }

    private void modeAParsing(Element element){
        String exp = element.getElementsByAttributeValue(BASE_KEY, EXP_KEY).first().text(),
                hpExp = element.getElementsByAttributeValue(BASE_KEY, HP_EXP_KEY).first().text(),
                examine = element.getElementsByAttributeValue(BASE_KEY, EXAMINE_KEY).first().text(),
                slayerExp = element.getElementsByAttributeValue(BASE_KEY, SLAYER_EXP_KEY).first().text(),
                level = element.getElementsByAttributeValue(BASE_KEY, LEVEL_KEY).first().text();
        try {
            String slayerCat = element.getElementsByAttributeValue(BASE_KEY, SLAYER_CAT_KEY).first().text();
            monster.setSlayercat(slayerCat);
        }
        catch (NullPointerException npe){
            //Ignore, no slayer info
        }

        monster.setCombatExp((float) NumberConverter.toDouble(exp));
        monster.setHpExp((float) NumberConverter.toDouble(hpExp));
        monster.setSlayerExp((float) NumberConverter.toDouble(slayerExp));
        monster.setMonsterID(monster.getMonsterID() + "_" + level);
        monster.setExamine(examine);
    }

    private void modeBParsing(Element element){
        String examineData = ElementDigger.dig(element, DiggerKeys.EXAMINE.sequence).text();
        String expData = ElementDigger.dig(element, true, DiggerKeys.EXP.sequence).text();
        String levelData = ElementDigger.dig(element, DiggerKeys.LEVEL.sequence).text();
        String trimmedExpData = expData.trim();
        String cbExpData, hpExpData;
        if (trimmedExpData.length() == 0) {
            cbExpData = lastCbExp + "";
            hpExpData = lastHpExp + "";
        } else {
            String[] expDataTokens = trimmedExpData.split(" ");
            cbExpData = expDataTokens[0];
            hpExpData = expDataTokens[2];
        }

        monster.setExamine(examineData);
        monster.setSlayercat(monster.getName());
        monster.setMonsterID(monster.getMonsterID() + "_" + levelData);
        lastHpExp = (float) NumberConverter.toDouble(hpExpData);
        monster.setHpExp(lastHpExp);
        lastCbExp = (float) NumberConverter.toDouble(cbExpData);
        monster.setCombatExp(lastCbExp);
        try{
            String slayerExpData = ElementDigger.dig(element, DiggerKeys.SLAYER_EXP.sequence).text().trim();
            lastSlayerExp = (slayerExpData.length() == 0 ? lastSlayerExp : (float) NumberConverter.toDouble(slayerExpData));
        } catch(IndexOutOfBoundsException ioob){
            //Ignored
        }
        monster.setSlayerExp(lastSlayerExp);

    }

    private void modeCParsing(Element element){
        String examineData = ElementDigger.dig(element,true, DiggerKeys.OLD_EXAMINE.sequence).text();
        String levelData = ElementDigger.dig(element, DiggerKeys.OLD_LEVEL.sequence).text();
        String cbExpData = ElementDigger.dig(element, DiggerKeys.OLD_CB_EXP.sequence).text();
        String hpExpData = ElementDigger.dig(element, DiggerKeys.OLD_HP_EXP.sequence).text();
        String slayerExpData = ElementDigger.dig(element, DiggerKeys.OLD_SLAYER_EXP.sequence).text();

        monster.setExamine(examineData);
        monster.setMonsterID(monster.getMonsterID() + "_" + levelData);
        monster.setSlayerExp((float) NumberConverter.toDouble(slayerExpData));
        lastHpExp = (float) NumberConverter.toDouble(hpExpData);
        monster.setHpExp(lastHpExp);
        lastCbExp = (float) NumberConverter.toDouble(cbExpData);
        monster.setCombatExp(lastCbExp);
    }
}
