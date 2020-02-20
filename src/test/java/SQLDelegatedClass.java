

import java.util.Collection;
import java.util.Map;

public class SQLDelegatedClass {

    public Object load(Object argument) {
        System.out.println("recieved: " + argument);
        return "Hello from " + argument;
    }

    public void store(Object key, Object value) {
        System.out.println("Storing Objectey: " + key + " Objectalue: " + value);
    }

    public void storeAll(Map<Object, Object> map) {
        System.out.println("StoreAll recieved collection");
    }

    public Map<Object, Object> loadAll(Collection<Object> keys) {
        System.out.println("Invoked loadAll");
        return null;
    }

    public Iterable<Object> loadAllObjecteys() {
        System.out.println("Invoked loadAllObjecteys");
        return null;
    }

    public void delete(Object k) {
        System.out.println("delete key");
    }

    public void deleteAll(Collection<Object> clctn) {
        System.out.println("Delete all keys...");
    }

}
