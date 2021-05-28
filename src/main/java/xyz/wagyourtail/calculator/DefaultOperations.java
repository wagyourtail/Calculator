package xyz.wagyourtail.calculator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultOperations {

    public static Map<String, Function<Double[], Double>> getFunctionOperations() {
        Map<String, Function<Double[], Double>> ops = new LinkedHashMap<>();

        return ops;
    }

    public static Map<String, Function<Double[], Double>> getSymbolOperations() {
        Map<String, Function<Double[], Double>> ops = new LinkedHashMap<>();
        ops.put("^", DefaultOperations::power);
        ops.put("*", DefaultOperations::multiply);
        ops.put("/", DefaultOperations::divide);
        ops.put("+",  DefaultOperations::add);
        ops.put("-", DefaultOperations::subtract);
        return ops;
    }

    public static double power(Double ...args) {
        double start = args[args.length - 1];
        for (int i = args.length - 2; i > -1; --i) {
            start = Math.pow(args[i], start);
        }
        return start;
    }

    public static double multiply(Double ...args) {
        double start = args[0];
        for (int i = 1; i < args.length; ++i) {
            start = start * args[i];
        }
        return start;
    }

    public static double divide(Double ...args) {
        double start = args[0];
        for (int i = 1; i < args.length; ++i) {
            start = start / args[i];
        }
        return start;
    }
    public static double add(Double ...args) {
        double start = args[0];
        for (int i = 1; i < args.length; ++i) {
            start = start + args[i];
        }
        return start;
    }

    public static double subtract(Double ...args) {
        double start = args[0];
        for (int i = 1; i < args.length; ++i) {
            start = start - args[i];
        }
        return start;
    }
}
