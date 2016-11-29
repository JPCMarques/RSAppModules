package pvm.monsterBuilding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slayer.Charm;
import slayer.CharmDropRate;
import slayer.Monster;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joaop on 26/10/2016.
 */
public class CharmProcessor extends StringProcessor<Monster.CharmList> {
    protected static enum DataRegex {
        CHARM_COUNT ("[c|C]harms.*\n"),
        KILL_COUNT ("[k|K]ills.*\n"),
        GOLD_COUNT ("[g|G]old.*\n"),
        GREEN_COUNT ("[g|G]reen.*\n"),
        CRIMSON_COUNT ("[c|C]rimson.*\n"),
        BLUE_COUNT ("[b|B]lue.*\n");

        public Pattern pattern;

        DataRegex(String regex){
            pattern = Pattern.compile(regex);
        }


    }
    protected HashMap<DataRegex, String> fetchedData;

    public CharmProcessor(Monster.CharmList charmList, String input) {
        super(charmList, input);
    }

    @Override
    public void process() throws IOException {
        fetchedData = new HashMap<>();
        for(DataRegex regex : DataRegex.values()){
            Matcher matcher = regex.pattern.matcher(input);
            String data = null;
            if(matcher.find()) data = input.substring(matcher.start(), matcher.end()).split("=")[1].trim();
            fetchedData.put(regex, data);
        }

        float killCount = Float.parseFloat(fetchedData.get(DataRegex.KILL_COUNT));
        int goldCount = Integer.parseInt(fetchedData.get(DataRegex.GOLD_COUNT)),
                greenCount = Integer.parseInt(fetchedData.get(DataRegex.GREEN_COUNT)),
                crimsonCount = Integer.parseInt(fetchedData.get(DataRegex.CRIMSON_COUNT)),
                blueCount = Integer.parseInt(fetchedData.get(DataRegex.BLUE_COUNT)),
                charmCount = Integer.parseInt(fetchedData.get(DataRegex.CHARM_COUNT));

        Logger logger = LogManager.getLogger();
        logger.debug("Charm data: kc {}, cc {}, go {}, gr {}, cr {}, bl {}", killCount, charmCount, goldCount, greenCount, crimsonCount,blueCount);

        float goldRate = goldCount / killCount,
                greenRate = greenCount / killCount,
                crimsonRate = crimsonCount / killCount,
                blueRate = blueCount / killCount;

        CharmDropRate goldDR = new CharmDropRate();
        goldDR.setBase(Charm.GOLD);
        goldDR.setRate(goldRate);
        target.getCharmDropRate().add(goldDR);
        CharmDropRate greenDR = new CharmDropRate();
        greenDR.setBase(Charm.GREEN);
        greenDR.setRate(greenRate);
        target.getCharmDropRate().add(greenDR);
        CharmDropRate crimsonDR = new CharmDropRate();
        crimsonDR.setBase(Charm.CRIMSON);
        crimsonDR.setRate(crimsonRate);
        target.getCharmDropRate().add(crimsonDR);
        CharmDropRate blueDR = new CharmDropRate();
        blueDR.setBase(Charm.BLUE);
        blueDR.setRate(blueRate);
        target.getCharmDropRate().add(blueDR);

    }
}
