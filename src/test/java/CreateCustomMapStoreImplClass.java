

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 *
 * @author Benjamin E Ndugga
 */
public class CreateCustomMapStoreImplClass {

    public static void main(String[] args) throws Exception {

        HazelcastInstance hazelcastInstance = newHazelcastInstance();

        ClassLoader systemClassLoader = hazelcastInstance.getClass().getClassLoader();
//        String keyClassName = "java.lang.String", valueClassName = "com.custom.classes.Person";
//        systemClassLoader.loadClass(keyClassName);
//        systemClassLoader.loadClass(valueClassName);
        Class<? extends GenericMapLoader> dynamicType = new ByteBuddy()
                .subclass(GenericMapLoader.class)
                .method(named("store"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .method(named("storeAll"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .method(named("load"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .method(named("loadAll"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .method(named("loadAllKeys"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .method(named("delete"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .method(named("deleteAll"))
                .intercept(MethodDelegation.to(new SQLDelegatedClass()))
                .make()
                .load(systemClassLoader, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

//        GenericMapLoader genericMapLoader = dynamicType.getDeclaredConstructor().newInstance();
//        genericMapLoader.load(5);
//        genericMapLoader.store("Key1", "Value1");
//        genericMapLoader.storeAll(null);
//        genericMapLoader.loadAll(null);
//        genericMapLoader.loadAllKeys();
//        genericMapLoader.delete(2);
//        genericMapLoader.deleteAll(null);

        IMap<Object, Object> map = hazelcastInstance.getMap("pnp.imsi");
        System.out.println("returned value from store: " + map.get(2));

    }

}//end of class

