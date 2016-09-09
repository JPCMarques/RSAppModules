package util.exceptions.dataMiner;

/**
 * Created by jpcmarques on 07-09-2016.
 */
public class InvalidInputException extends DataMinerException {
    protected static final String BASE_MESSAGE = "Error during input pre-processing";

    public InvalidInputException(String reason) {
        super(BASE_MESSAGE, reason + ".");
    }

    public InvalidInputException(){
        super(BASE_MESSAGE);
    }
}
