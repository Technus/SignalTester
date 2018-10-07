package java.com.github.technus.signalTester.functionalInterfaces;

@FunctionalInterface
public interface ISupplier<V> {
    V get() throws Exception;
}
