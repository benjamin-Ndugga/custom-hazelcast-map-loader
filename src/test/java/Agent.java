

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

/**
 *
 * @author Benjamin E Ndugga
 */
public class Agent  {

    public static void main(String args, Instrumentation inst) {

        
        
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                DynamicType.Unloaded unloadedType = new ByteBuddy()
                        .subclass(Object.class)
                        .method(ElementMatchers.isToString())
                        .intercept(FixedValue.value("Hello World ByteBuddy!"))
                        .make();

                Class<?> dynamicType = unloadedType
                        .load(getClass().getClassLoader()).getLoaded();

                return null;
            }
        });
    }

}//end of AgentClass
