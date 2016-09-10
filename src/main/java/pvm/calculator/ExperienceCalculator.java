package pvm.calculator;

import slayer.Monster;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class ExperienceCalculator {
    public static class ExperienceValues{
        public float slayer, hp, combat;
    }

    public static ExperienceValues calcExperience (Monster monster, int killNumber){
        ExperienceValues vals = new ExperienceValues();
        vals.slayer = monster.getSlayerExp()*killNumber;
        vals.hp = monster.getHpExp()*killNumber;
        vals.combat = monster.getCombatExp()*killNumber;
        return vals;
    }
}
