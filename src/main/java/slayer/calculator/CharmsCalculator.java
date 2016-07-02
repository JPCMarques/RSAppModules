package slayer.calculator;

import slayer.Charm;
import slayer.CharmDropRate;
import slayer.Monster;

import java.util.List;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class CharmsCalculator {
    private static class CharmCount{
        public float gold, green, crimson, blue;
    }

    public static CharmCount calcCharmCount (Monster monster, int killNumber){
        CharmCount charmCount = new CharmCount();
        Monster.CharmList charmList = monster.getCharmList();
        Monster.CharmList charmListRef = (Monster.CharmList) charmList.getRef();
        List<CharmDropRate> cdrs = (charmListRef == null ? charmList.getCharmDropRate() : charmListRef.getCharmDropRate());
        for(CharmDropRate cdr : cdrs){
            Charm charm = cdr.getBase();
            float rate = cdr.getRate();
            if(charm == Charm.GOLD) charmCount.gold = rate*killNumber;
            else if(charm == Charm.GREEN) charmCount.green = rate*killNumber;
            else if(charm == Charm.CRIMSON) charmCount.crimson = rate*killNumber;
            else if(charm == Charm.BLUE) charmCount.blue = rate*killNumber;
        }
        return charmCount;
    }
}
