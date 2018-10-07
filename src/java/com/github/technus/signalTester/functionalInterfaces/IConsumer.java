package java.com.github.technus.signalTester.functionalInterfaces;

@FunctionalInterface
public interface IConsumer<ARG> {
    void eat(ARG arg) throws Exception;
}
