package slayer;

import java.io.*;
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

    public static void save(ThrottledUpdateManager throttledUpdateManager) throws IOException {
        FileOutputStream fis = new FileOutputStream(ThrottledUpdateManager.class.getName());
        save(fis, throttledUpdateManager);
    }

    public static void save(OutputStream outputStream, ThrottledUpdateManager throttledUpdateManager) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(throttledUpdateManager);
        oos.close();
        outputStream.close();
    }

    public static ThrottledUpdateManager load() throws IOException, ClassNotFoundException {
        File src = new File(ThrottledUpdateManager.class.getName());
        if(!src.exists()) return new ThrottledUpdateManager();
        FileInputStream fis = new FileInputStream(src);
        return load(fis);
    }

    public static ThrottledUpdateManager load(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        ThrottledUpdateManager throttledUpdateManager = (ThrottledUpdateManager) ois.readObject();
        ois.close();
        inputStream.close();
        return throttledUpdateManager;
    }
}
