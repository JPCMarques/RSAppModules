package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by jpcmarques on 09-09-2016.
 */
public class IDLogger {
    private String loggingID;
    private Logger logger;

    public IDLogger(Class<?> name, String loggingID) {
        this.loggingID = loggingID;
        logger = LogManager.getLogger(name);
    }

    public void i(String message){
        logger.info(loggingID + ": " + message);
    }

    public void w(String message){
        logger.warn(loggingID + ": " + message);
    }

    public void d(String message){
        logger.debug(loggingID + ": " + message);
    }

    public void e(String message){
        logger.error(loggingID + ": " + message);
    }

}
