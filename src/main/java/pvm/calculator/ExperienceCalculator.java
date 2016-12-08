package pvm.calculator;

import pvm.Monster;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class ExperienceCalculator {
    public static class ExperienceValues{
        public float slayer, hp, combat;
        public String toString(){
            return "Slayer experience: " + slayer + "\nHp experience: " + hp + "\nCombat exp: "+combat;
        }
    }

    public static ExperienceValues calcExperience (Monster monster, int killNumber){
        ExperienceValues vals = new ExperienceValues();
        vals.slayer = monster.getSlayerExp()*killNumber;
        vals.hp = monster.getHpExp()*killNumber;
        vals.combat = monster.getCombatExp()*killNumber;
        return vals;
    }
}
