package util;

import org.jsoup.nodes.Element;

import java.io.OutputStream;

/**
 * Created by jpcmarques on 28-09-2016.
 */
public abstract class ElementDigger {
    public static Element dig(Element element, boolean print, int... indexes) {
        Element retVal = element;
        for(int i : indexes){
            if(print) System.out.println("LOOKING FOR INDEX: " + i + " IN:\n" + retVal);
            retVal = retVal.child(i);
        }
        return retVal;
    }

    public static Element dig(Element element, int... indexes){
        return dig(element, false, indexes);
    }
}
