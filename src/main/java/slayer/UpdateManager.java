package slayer;

import java.io.Serializable;

/**
 * Created by jpcmarques on 27-06-2016.
 */
public interface UpdateManager extends Serializable {
    public static final long SECOND = 1000, MINUTE = 60 * SECOND, HOUR = 60 * MINUTE;

    boolean canUpdate(String key);
    void updated(String key);
    boolean update(String key);
}
