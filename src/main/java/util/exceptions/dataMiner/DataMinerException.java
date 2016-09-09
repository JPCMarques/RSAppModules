package util.exceptions.dataMiner;

/**
 * Created by jpcmarques on 07-09-2016.
 */
public class DataMinerException extends Exception {
    public DataMinerException(String s, String reason) {
        super(s + ". Reason: " + reason + ".");
    }

    public DataMinerException(String s){
        this(s, "unspecified");
    }
}
