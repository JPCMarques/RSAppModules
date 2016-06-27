package slayer;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by jpcmarques on 27-06-2016.
 */
public class ThrottledUpdateManager implements UpdateManager{
    private HashMap<String, Date> monsterLastUpdate = new HashMap<>();
    private long minimumUpdateInterval;

    public ThrottledUpdateManager(){
        minimumUpdateInterval = HOUR;
    }

    @Override
    public boolean canUpdate(String key) {
        return !monsterLastUpdate.containsKey(key) ||
                new Date().getTime() - monsterLastUpdate.get(key).getTime() > minimumUpdateInterval;
    }

    @Override
    public void updated(String key) {
        monsterLastUpdate.put(key, new Date());
    }

    @Override
    public boolean update(String key) {
        if(canUpdate(key)) {
            updated(key);
            return true;
        }
        return false;
    }
}
