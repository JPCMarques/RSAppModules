package util.exceptions.dataMiner;

/**
 * Created by jpcmarques on 08-09-2016.
 */
public class InvalidResultException extends DataMinerException {
    protected static final String BASE_MESSAGE = "Result produced is invalid";
    public InvalidResultException(String reason) {
        super(BASE_MESSAGE, reason);
    }

    public InvalidResultException() {
        super(BASE_MESSAGE);
    }
}
