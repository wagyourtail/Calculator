package xyz.wagyourtail.calculator;

public interface FunctionWithException<T, R> {
    R apply(T arg0) throws Exceptions.CalculationException;

}
