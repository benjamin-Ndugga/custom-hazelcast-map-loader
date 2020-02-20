

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Benjamin E Ndugga
 * @param <K>
 * @param <V>
 */
public class GenericMapLoader<K extends Object, V extends Object> implements MapLoaderLifecycleSupport, MapLoader<K, V> {

    @Override
    public void init(HazelcastInstance hi, Properties properties, String mapName) {
        //set up the connection details to the DB
    }

    @Override
    public void destroy() {
        System.out.println("Invoked from " + getClass().getName());
        //close conneting the DB
    }

    public void test(int i) {
    }

    @Override
    public V load(K k) {
        System.out.println("Load {Passed argument: " + k.getClass().getName() + "}");
        //load Key to HZ
        return null;
    }

    @Override
    public Map<K, V> loadAll(Collection<K> clctn) {
        //load all entries to HZ
        return null;
    }

    @Override
    public Iterable<K> loadAllKeys() {
        //load all Keys
        return null;
    }

}
