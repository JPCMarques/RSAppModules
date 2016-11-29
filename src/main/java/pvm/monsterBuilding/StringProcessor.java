package pvm.monsterBuilding;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by joaop on 25/10/2016.
 */
public abstract class StringProcessor<Target> {

    protected Target target;
    protected String input;

    public StringProcessor(Target target, String input) {
        this.target = target;
        this.input = input;
    }

    public abstract void process() throws IOException;
    public final Target getTarget() {return target;}
    public static final String getLineRegex(String baseRegex){
        return "^" + baseRegex + "$";
    }
}
