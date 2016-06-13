package slayer;

/**
 * Created by jpcmarques on 13-06-2016.
 */
public abstract class ExperienceCalculator {
    private static class ExperienceValues{
        public float slayer, hp, combat;
    }

    public static ExperienceValues calcExperience (Monster monster, int taskSize){
        ExperienceValues vals = new ExperienceValues();
        vals.slayer = monster.getSlayerExp()*taskSize;
        vals.hp = monster.getHpExp()*taskSize;
        vals.combat = monster.getCombatExp()*taskSize;
        return vals;
    }
}
