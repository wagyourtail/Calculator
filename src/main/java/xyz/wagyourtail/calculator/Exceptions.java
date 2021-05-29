package xyz.wagyourtail.calculator;

public class Exceptions {
    public static abstract class CalculationException extends Exception {
        public CalculationException(String s) {
            super(s);
        }
    }

    public static class UnknownSymbolException extends CalculationException {

        public UnknownSymbolException(String expression) {
            super("Unknown symbol(s) in expression \"" + expression + "\"");
        }
    }

    public static class UnknownOperationException extends CalculationException {

        public UnknownOperationException(String expression) {
            super("Unknown operation(s) in expression \"" + expression + "\"");
        }

    }

    public static class UnknownFunctionException extends CalculationException {
        public UnknownFunctionException(String expression) {
            super("Unknown function in expression \"" + expression + "\"");
        }

    }

    public static class FunctionParameterCountException extends CalculationException {
        public FunctionParameterCountException(String fName, int expected, int actual) {
            super("Function \"" + fName + "\" expected " + expected + " parameters, got " + actual);
        }
    }

    public static class BitwiseCalculationException extends CalculationException {
        public BitwiseCalculationException(double input) {
            super("expected integer input for bitwise operation, got \"" + input +"\"");
        }
    }
}
