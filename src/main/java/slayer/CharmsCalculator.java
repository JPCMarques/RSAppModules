package slayer;

import java.util.List;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class CharmsCalculator {
    private static class CharmCount{
        public float gold, green, crimson, blue;
    }

    public static CharmCount calcCharmCount (Monster m, int taskLength){
        CharmCount charmCount = new CharmCount();
        List<CharmDropRate> cdrs = m.getCharmList().getCharmDropRate();
        for(CharmDropRate cdr : cdrs){
            Charm charm = cdr.getBase();
            float rate = cdr.getRate();
            if(charm == Charm.GOLD) charmCount.gold = rate*taskLength;
            else if(charm == Charm.GREEN) charmCount.green = rate*taskLength;
            else if(charm == Charm.CRIMSON) charmCount.crimson = rate*taskLength;
            else if(charm == Charm.BLUE) charmCount.blue = rate*taskLength;
        }
        return charmCount;
    }
}
