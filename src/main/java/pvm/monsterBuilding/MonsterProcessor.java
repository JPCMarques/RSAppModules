package pvm.monsterBuilding;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slayer.*;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joaop on 25/10/2016.
 */
public class MonsterProcessor extends StringProcessor<DropData> {
    protected static final String infoboxRegex = "\\{\\{\\s*[I|i]nfobox\\s*[m|M]onster.*\n";

    protected static enum DataRegex {
        LEVEL,EXPERIENCE,EXAMINE,SLAYLVL,SLAYXP, SLAYERCAT, ASSIGNED_BY;
        public String regex;

        DataRegex() {
            this.regex = "\\|\\s*" + this.name().toLowerCase() + ".*\n";
        }

        @Override
        public String toString() {
            return regex.replace("\n", "");
        }

        public Pattern getPattern() {
            return Pattern.compile(regex);
        }
    }

    //Used if slayer level exists, but assigned by doesnt
    protected static enum MasterRegex {
        TURAEL("([t|T]urael|[S|s]pria)", SlayerMaster.TURAEL_SPRIA),
        MAZCHNA("([m|M]azchna|[A|a]chtryn)", SlayerMaster.MAZCHNA_ACHTRYN),
        VANNAKA("[V|v]annaka", SlayerMaster.VANNAKA),
        DURADEL("([D|d]uradel|[L|l]apalok)", SlayerMaster.DURADEL_LAPALOK),
        CHAELDAR("[C|c]haeldar", SlayerMaster.CHAELDAR),
        SUMONA("[S|s]umona", SlayerMaster.SUMONA),
        KURADAL("[K|k]uradal", SlayerMaster.KURADAL),
        MORVRAN("[M|m]orvran", SlayerMaster.MORVRAN)
        ;
        public String regex;
        public SlayerMaster correspondant;

        MasterRegex(String regex, SlayerMaster correspondant){
            this.correspondant = correspondant;
            this.regex = regex;
        }

        @Override
        public String toString() {
            return regex.replace("\n", "");
        }

        public Pattern getPattern(){
            return Pattern.compile(regex);
        }
    }

    protected final String URL_START = "http://runescape.wikia.com/wiki/",
            CHARM_TOKEN = "Charm:",
            URL_END = "?action=raw";
    protected String monsterRawData, charmRawData, monsterID;
    protected List<Monster> monsterList;
    protected Monster.CharmList charmList;
    protected DropTable dropTable;
    protected HashMap<String, Monster> existingMonsters;
    protected HashMap<DataRegex, String> fetchedData;
    protected HashMap<DataRegex, String[]> processedData;
    protected Logger logger;
    protected Monster.MasterList masterList = new Monster.MasterList();
    protected boolean masterListProcessed = false;
    protected int monsterCount;
    protected ArchetypeMonsterList.Archetype slayerCat;

    public MonsterProcessor(DropData dropData, String input) {
        super(dropData, input);
        init();
    }

    public void newInput(String input){
        this.input = input;
        init();
    }

    public void init(){
        monsterID = input.replace(" ", "_");
        monsterList = new LinkedList<>();
        charmList = new Monster.CharmList();
        dropTable = new DropTable();
        fetchedData = new HashMap<>();
        logger = LogManager.getLogger();
        if(target.getArchetypeMonsterList() == null) target.setArchetypeMonsterList(new ArchetypeMonsterList());
        if(target.getSlayerMasterAssignments() == null) target.setSlayerMasterAssignments(new SlayerMasterAssignments());
        if(target.getMonsterList() == null) target.setMonsterList(new MonsterList());
        if(target.getItemList() == null) target.setItemList(new ItemList());
        existingMonsters = new HashMap<>();
        for(Monster m : target.getMonsterList().getMonster()){
            existingMonsters.put(m.getMonsterID(), m);
        }
    }

    @Override
    public void process() throws IOException {
        logger.debug("Initiating monster stats processing for {}...", monsterID);
        boolean charmPageExists = true;
        logger.debug("Initiating input streams for raw data...");
        InputStream monsterPageStream = new URL(URL_START+monsterID+URL_END).openStream();
        InputStream charmPageStream = null;
        try{
            charmPageStream = new URL(URL_START+CHARM_TOKEN+monsterID+URL_END).openStream();
        } catch (FileNotFoundException fnfe){
            charmPageExists = false;
        }
        logger.debug("Done.");

        logger.debug("Fetching raw data from input streams...");
        monsterRawData = IOUtils.toString(new InputStreamReader(monsterPageStream));
        if (monsterRawData.startsWith("#REDIRECT")){
            monsterID = monsterRawData.substring(monsterRawData.lastIndexOf("[")+1, monsterRawData.indexOf("]")).replace(" ", "_");
            logger.debug("Redirecting to new page and restarting processing for monsterID {}...", monsterID);
            monsterPageStream.close();
            if (charmPageExists) charmPageStream.close();
            process(monsterID);
            return;
        }
        if(charmPageExists) charmRawData = IOUtils.toString(new InputStreamReader(charmPageStream));
        logger.debug("Done.\n\n--==MONSTER RAW DATA==--\n{}\n\n--==CHARM RAW DATA==--\n{}", monsterRawData, charmRawData);

        logger.debug("Closing input streams...");
        IOUtils.closeQuietly(monsterPageStream);
        if(charmPageExists) IOUtils.closeQuietly(charmPageStream);
        logger.debug("Done.");

        logger.debug("Filtering necessary data...");
        for(DataRegex dataRegex: DataRegex.values()){
            fetchedData.put(dataRegex, null);
            Matcher dataMatcher = dataRegex.getPattern().matcher(monsterRawData);
            if(!dataMatcher.find()) {
                logger.debug("Found no data for regex {}", dataRegex.toString());
                continue;
            }
            String matchedData = monsterRawData.substring(dataMatcher.start(), dataMatcher.end());
            matchedData = removeSpecialCharacters(matchedData);
            logger.debug("Data found for regex {}:\n{}", dataRegex.toString(), matchedData);
            fetchedData.put(dataRegex, matchedData.substring(1));
        }
        logger.info("Initiating mosnter stats processing...");
        monsterStatsProcessing();
        logger.info("Done.");
        logger.info("Initiating drop table processing...");
        dropTableProcessing();
        logger.info("Done.");
        if(charmPageExists){
            logger.info("Initiating charm list processing...");
            charmListProcessing();
            logger.info("Done.");
        }
        logger.info("Initiating monster building...");
        buildMonster();
        logger.info("Done.");
    }

    protected void charmListProcessing() throws IOException {
        CharmProcessor charmProcessor = new CharmProcessor(charmList, charmRawData);
        charmProcessor.process();
    }

    protected void dropTableProcessing() throws IOException {
        DropTableProcessor dropTableProcessor = new DropTableProcessor(dropTable, monsterRawData, target.getItemList());
        dropTableProcessor.process();
    }

    protected void monsterStatsProcessing(){
        int count = 1;
        logger.debug("Retrieving monster count...");
        for(String s: fetchedData.values()){
            if(s == null || !s.contains("|")) continue;
            logger.debug("Counting monster data in: {}", s);
            String[] data = s.split("\\|");
            logger.debug("Counted {} monsters", data.length);
            if(data.length > count) count = data.length;
        }
        logger.debug("Obtained monster count of: {}", count);

        processedData = new HashMap<>();
        logger.info("Initiating stats data processing");
        for(DataRegex regex : fetchedData.keySet()){
            String regexData = fetchedData.get(regex);
            logger.info("Processing {} data...", regex.name());
            if (regexData != null){
                processedData.put(regex, postProcessData(processData(regex, regexData, count)));
            }
            else {
                logger.debug("Found empty data, defaulting to null...");
                processedData.put(regex, null);
            }
            logger.info("Done.");
        }
        logger.info("All stats data has been processed.");

        monsterCount = count;
    }



    private String[] processData(DataRegex dataRegex, String rawData, int length){
        String[] data = new String[length];

        if(!rawData.contains("|")) {
            logger.debug("Singular data found for {} in:\n{}", dataRegex.name(), rawData);
            String value = rawData.split("=")[1].trim();
            for(int i = 0; i < data.length; i++) data[i] = value;
        }else{
            logger.debug("Multiple data found for {} in:\n{}", dataRegex.name(), rawData);
            String filteredRawData = rawData.replace(dataRegex.name().toLowerCase(), "");
            String[] splitRawData = filteredRawData.split("\\|");
            for(String s : splitRawData){
                logger.debug("Processing data item {}...", s);
                String[] dataItem = s.trim().split("=");
                int index = Integer.parseInt(dataItem[0].trim()) - 1;

                String target = (dataItem.length == 1 ? "$" + (index - 1) : dataItem[1].trim());
                if(target.startsWith("$")){
                    logger.debug("Found backwards reference: {}", target);
                    data[index] = data[Integer.parseInt(target.substring(1)) - 1];
                }
                else
                    data[index] = target;
                logger.debug("Done.");
            }
        }
        return data;
    }

    private String[] postProcessData(String[] data){
        String[] processedData = new String[data.length];
        logger.debug("Initiating post processing of data...");
        for(int i = 0; i < data.length; i++){
            if(data[i] == null){
                logger.debug("Data missing for index {}", i);
                for(int ii = 1; ii < data.length / 2; ii++){
                    int back = i-ii;
                    int front = i+ii;
                    if(back >= 0 && data[back] != null){
                        logger.debug("Using data found behind missing index as replacement:\n{}", data[back]);
                        processedData[i] = data[back];
                        break;
                    }
                    else if (front <= data.length && data[front] != null){
                        logger.debug("Using data found in front of missing index as replacement:\n{}", data[front]);
                        processedData[i] = data[front];
                        break;
                    }
                }
            }
            else processedData[i] = data[i];
            processedData[i] = processedData[i].replace(",", "");
        }
        logger.debug("Done.");
        return processedData;
    }

    private void buildMonster(){
        for(int i = 0; i < monsterCount; i++){
            Monster monster = new Monster();
            monster.setCharmList(charmList);
            monster.setDropTable(dropTable);


            int level = Integer.parseInt(processedData.get(DataRegex.LEVEL)[i]);
            String examine = processedData.get(DataRegex.EXAMINE)[i];
            String name = input;
            String monsterID = input.replace(" ", "_") + level;
            float combatExp = Float.parseFloat(processedData.get(DataRegex.EXPERIENCE)[i]);
            float hpExp = combatExp/3;
            examine = removeSpecialCharacters(examine);
            if(existingMonsters.get(monsterID) == null){
                target.getMonsterList().getMonster().add(monster);
                existingMonsters.put(monsterID, monster);
            }


            monster.setLevel(level);
            monster.setExamine(examine);
            monster.setName(name);
            monster.setMonsterID(monsterID);
            monster.setCombatExp(combatExp);
            monster.setHpExp(hpExp);


            String[] slaylvlData = processedData.get(DataRegex.SLAYLVL);
            if(slaylvlData == null) continue;

            int slayerLvl = Integer.parseInt(slaylvlData[i]);
            float slayExp = Float.parseFloat(processedData.get(DataRegex.SLAYXP)[i]);

            String[] assignedBy = processedData.get(DataRegex.ASSIGNED_BY);
            logger.info("Processing masters for {}...", name);
            if(assignedBy == null)
                processAltMasters(monster);
            else
                processMasters(monster, assignedBy[i]);
            logger.info("Done.");

            String[] slayerCatData = processedData.get(DataRegex.SLAYERCAT);
            String slayerCatID;
            if (slayerCatData == null) slayerCatID = name.replace(" ", "_").toLowerCase();
            else slayerCatID = slayerCatData[i];


            slayerCatID = removeSpecialCharacters(slayerCatID);
            monster.setSlayerLevel(slayerLvl);
            monster.setSlayerExp(slayExp);
            monster.setMasterList(masterList);
            ArchetypeMonsterList.Archetype archetype = processSlayerCat(slayerCatID, monster.getMasterList().getMaster());
            monster.setSlayercat(archetype);

            boolean containsArchMonster = false;
            for (ArchetypeMonsterList.Archetype.ArchetypeMonster archetypeMonster : archetype.getArchetypeMonster()){
                if(((Monster) archetypeMonster.getMonsterID()).getMonsterID().equals(monster.getMonsterID())){
                    containsArchMonster = true;
                    break;
                }
            }
            if(!containsArchMonster){
                ArchetypeMonsterList.Archetype.ArchetypeMonster archetypeMonster = new ArchetypeMonsterList.Archetype.ArchetypeMonster();
                archetypeMonster.setMonsterID(monster);
                archetype.getArchetypeMonster().add(archetypeMonster);
            }


        }
    }

    private String removeSpecialCharacters (String data ){
        return data.replace("{{", "").replace("}}", "").replace("noseo|", "");
    }

    private ArchetypeMonsterList.Archetype processSlayerCat(String slayerCatID, List<SlayerMaster> masters){
        if (slayerCat != null) return slayerCat;
        for(ArchetypeMonsterList.Archetype archetype : target.getArchetypeMonsterList().getArchetype()){
            if (archetype.getArchetypeID().equals(slayerCatID)) slayerCat = archetype;
        }
        if(slayerCat == null){
            slayerCat = new ArchetypeMonsterList.Archetype();
            slayerCat.setArchetypeID(slayerCatID);
            target.getArchetypeMonsterList().getArchetype().add(slayerCat);
        }
        for(SlayerMaster master : masters) {
            boolean slayerMasterFound = false;
            for(SlayerMasterAssignments.SlayerMaster slayerMaster: target.getSlayerMasterAssignments().getSlayerMaster()){
                if(slayerMaster.getName() == master){
                    slayerMasterFound = true;
                    boolean assignmentFound = false;
                    for(SlayerMasterAssignments.SlayerMaster.Assignment assignment : slayerMaster.getAssignment()){
                        ArchetypeMonsterList.Archetype assignmentArchetype = (ArchetypeMonsterList.Archetype) assignment.getArchetype();
                        if(assignmentArchetype.getArchetypeID().equals(slayerCatID)){
                            assignmentFound = true;
                            break;
                        }
                    }
                    if(!assignmentFound) {
                        generateAssignment(slayerCat, slayerMaster);
                    }
                    break;
                }
            }
            if(!slayerMasterFound){
                SlayerMasterAssignments.SlayerMaster slayerMaster = new SlayerMasterAssignments.SlayerMaster();
                slayerMaster.setName(master);
                generateAssignment(slayerCat, slayerMaster);
                target.getSlayerMasterAssignments().getSlayerMaster().add(slayerMaster);
            }
        }
        return slayerCat;
    }

    private void generateAssignment(ArchetypeMonsterList.Archetype archetype, SlayerMasterAssignments.SlayerMaster slayerMaster){
        SlayerMasterAssignments.SlayerMaster.Assignment assignment = new SlayerMasterAssignments.SlayerMaster.Assignment();
        assignment.setArchetype(archetype);
        slayerMaster.getAssignment().add(assignment);
    }

    private void processMasters (Monster m, String masterData){
        if(masterListProcessed) return;
        masterList = new Monster.MasterList();
        for(MasterRegex masterRegex : MasterRegex.values()){
            Pattern masterPattern = masterRegex.getPattern();
            Matcher matcher = masterPattern.matcher(masterData);
            if (matcher.find()) masterList.getMaster().add(masterRegex.correspondant);
        }
        masterListProcessed = true;
    }

    private void processAltMasters(Monster m){
        processMasters(m, monsterRawData);
    }

    public void process(String input) throws IOException {
        newInput(input);
        process();
    }
}
