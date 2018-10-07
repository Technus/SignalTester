package java.com.github.technus.signalTester.functionalInterfaces;

@FunctionalInterface
public interface IFunction<K,V>{
    V act(K k) throws Exception;
}
