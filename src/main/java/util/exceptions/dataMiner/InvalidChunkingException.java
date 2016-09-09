package util.exceptions.dataMiner;

/**
 * Created by jpcmarques on 08-09-2016.
 */
public class InvalidChunkingException extends DataMinerException {
    protected static final String BASE_MESSAGE = "Error during chunk operation";
    public InvalidChunkingException(String reason) {
        super(BASE_MESSAGE, reason);
    }

    public InvalidChunkingException() {
        super(BASE_MESSAGE);
    }
}
