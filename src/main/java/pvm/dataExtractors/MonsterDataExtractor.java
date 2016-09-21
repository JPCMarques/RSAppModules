package pvm.dataExtractors;

import slayer.Monster;

/**
 * Created by jpcmarques on 10-09-2016.
 */
public abstract class MonsterDataExtractor<Output> extends RSWikiTableExtractor<Output> {
    protected Monster monster;

    public MonsterDataExtractor(String input, String classFilter, Monster monster) {
        super(input, classFilter);
        this.monster = monster;
    }
}
