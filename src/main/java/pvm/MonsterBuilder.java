package pvm;

import slayer.DropData;
import slayer.Monster;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
        private String key;

        CharmsPattern(String key){
            this.key = key;
        }

        public Pattern getPattern(){
            return Pattern.compile(key);
        }
    }

    public MonsterBuilder(DropData dropData, String monsterName){
        this.dropData = dropData;
        this.monsterName = monsterName;
    }

    private void processCharms() throws IOException {
        String charmURL = URL_START + CHARMS_TOKEN + monsterName.replace(" ", "_") + URL_END;
        InputStream charmDataStream = new URL(charmURL).openStream();
    }

}
