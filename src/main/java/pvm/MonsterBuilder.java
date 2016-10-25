package pvm;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slayer.Charm;
import slayer.CharmDropRate;
import slayer.DropData;
import slayer.Monster;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpcmarques on 18-10-2016.
 */
public class MonsterBuilder {
    private DropData dropData;
    private Monster monster;
    private String monsterName;
    private static final String URL_START = "http://runescape.wikia.com/wiki/",
        CHARMS_TOKEN = "Charm:",
        URL_END = "?action=raw";
    private static final float conxpRate = 0.33f;
    private static final String rsStringRegex = "[\\s\\w()\\|\\{}]++";
    private Pattern monsterTypePattern = Pattern.compile("\\{Infobox monster " + rsStringRegex + "\n");

    private enum StatsPattern {
        NAME("name", true),
        EXAMINE("examine"),
        LEVEL("level"),
        SLAYERCAT("slayercat"),
        SLAYEXP("slayxp"),
        EXP("experience"),
        ASSIGNED_BY("assigned_by")
        ;
        StatsPattern(String key, boolean isSingle){
            this.key = key;
            regex = "\\|" + key + "\\s*+=" + rsStringRegex + "\n";
        }

        StatsPattern(String key){
            this(key, false);
        }
        private String key;
        private String regex;

        public Pattern getPattern(){
            return Pattern.compile(regex);
        }
        public Pattern getMultiPattern(){
            return Pattern.compile(regex.replace(key, key+"\\d*"));
        }
    }
    
    private enum DropsPattern {
        NAME("\\|Name\\s*=" + rsStringRegex),
        QUANTITY("\\|Quantity\\s*=\\s*\\d+\\s*(\\(noted\\))*"),
        RARITY("\\|Rarity\\s*=\\s*(Uncommon|Common|Rare|Always|Very rare)"),
        OUTLIER_RATES("\\d+/\\d+ drop chance");
        private String key;

        DropsPattern(String key){
            this.key = key;
        }

        public Pattern getPattern() {
            return Pattern.compile(key);
        }
    }

    private enum CharmsPattern {
        CHARM_COUNT("\\|charms\\s*=\\s*\\d+"),
        KILL_COUNT("\\|kills\\s*=\\s*\\d+"),
        GOLD_COUNT("\\|gold\\s*=\\s*\\d+"),
        GREEN_COUNT("\\|green\\s*=\\s*\\d+"),
        CRIMSON_COUNT("\\|crimson\\s*=\\s*\\d+"),
        BLUE_COUNT("\\|blue\\s*=\\s*\\d+"),
        ;
        public String key;

        CharmsPattern(String key){
            this.key = key;
        }

        public Pattern getPattern(){
            return Pattern.compile(key);
        }

        public Matcher getMatcher(String data) {
            return getPattern().matcher(data);
        }
    }

    public MonsterBuilder(DropData dropData, String monsterName){
        this.dropData = dropData;
        this.monsterName = monsterName;
        monster = new Monster();
        monster.setName(monsterName);
    }

    private void processCharms() throws IOException {
        Logger logger = LogManager.getLogger();

        float charmcount = 0, killcount = 0; //Force floating point operations for decimal precision
        int gold = 0, green = 0, crimson = 0, blue = 0;

        String charmURL = URL_START + CHARMS_TOKEN + monsterName.replace(" ", "_") + URL_END;
        logger.debug("Monster charm URL: " + charmURL);

        logger.debug("Initiating charm data fetch...");
        InputStream charmDataStream = new URL(charmURL).openStream();
        String charmStringData = IOUtils.toString(new InputStreamReader(charmDataStream));
        Monster.CharmList charmList = new Monster.CharmList();
        logger.debug("Done. Raw data for charms: \n" + charmStringData);

        logger.debug("Initiating regex searches...");
        for(CharmsPattern cp : CharmsPattern.values()){
            Matcher matcher = cp.getMatcher(charmStringData);
            matcher.find();
            String match = charmStringData.substring(matcher.start(), matcher.end());
            logger.debug("Charm pattern match for " + cp.key +  ":\n" + match);
            String data = match.split("=")[1];
            switch(cp){
                case CHARM_COUNT:
                    charmcount = Integer.parseInt(data);
                    break;
                case KILL_COUNT:
                    killcount = Integer.parseInt(data);
                    break;
                case GOLD_COUNT:
                    gold = Integer.parseInt(data);
                    break;
                case GREEN_COUNT:
                    green = Integer.parseInt(data);
                    break;
                case CRIMSON_COUNT:
                    crimson = Integer.parseInt(data);
                    break;
                case BLUE_COUNT:
                    blue = Integer.parseInt(data);
                    break;
            }
        }
        logger.debug("Done.");

        CharmDropRate gdr = new CharmDropRate(),
                grdr = new CharmDropRate(),
                cdr = new CharmDropRate(),
                bdr = new CharmDropRate();

        logger.debug("Initiating charm drop rates...");
        gdr.setRate(gold/killcount*charmcount);
        grdr.setRate(green/killcount*charmcount);
        cdr.setRate(crimson/killcount*charmcount);
        bdr.setRate(blue/killcount*charmcount);

        gdr.setBase(Charm.GOLD);
        grdr.setBase(Charm.GREEN);
        cdr.setBase(Charm.CRIMSON);
        bdr.setBase(Charm.BLUE);
        logger.debug("Done. Charm drop rates:\n" +
                "Gold charms: " + gdr.getRate() + "\n"+
                "Green charms: " + grdr.getRate() + "\n"+
                "Crimson charms: " + cdr.getRate() + "\n"+
                "Blue charms: " + bdr.getRate() + "\n");

        charmList.getCharmDropRate().add(gdr);
        charmList.getCharmDropRate().add(grdr);
        charmList.getCharmDropRate().add(cdr);
        charmList.getCharmDropRate().add(bdr);

        logger.debug("Initiating monster's charmList with fetched data...");
        monster.setCharmList(charmList);
        logger.debug("Done.");
    }
    private void processMonsterStats() throws IOException {

    }

}
