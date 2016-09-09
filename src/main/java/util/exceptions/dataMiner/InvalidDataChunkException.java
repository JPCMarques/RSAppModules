package util.exceptions.dataMiner;

/**
 * Created by jpcmarques on 07-09-2016.
 */
public class InvalidDataChunkException extends DataMinerException {
    protected static final String BASE_MESSAGE = "Error during data chunk pre-processing";
    public InvalidDataChunkException(String reason) {
        super(BASE_MESSAGE, reason);
    }

    public InvalidDataChunkException() {
        super(BASE_MESSAGE);
    }
}
