package xyz.wagyourtail.calculator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultOperations {

    public static Map<String, Function<String[], String>> getFunctionOperations() {
        Map<String, Function<String[], String>> ops = new LinkedHashMap<>();
        ops.put("cos", DefaultOperations::cos);
        ops.put("sin", DefaultOperations::sin);
        ops.put("tan", DefaultOperations::tan);
        return ops;
    }

    public static Map<String, Function<String, String>> getSymbolOperations() {
        Map<String, Function<String, String>> ops = new LinkedHashMap<>();
        ops.put("\\^", DefaultOperations::power);
        ops.put("[*/%]", DefaultOperations::multiplyOrDivideOrModulo);
        ops.put("[+-]",  DefaultOperations::addOrSubtract);
        return ops;
    }

    public static Map<String, Double> getConstants() {
        Map<String, Double> constants = new LinkedHashMap<>();
        constants.put("pi", Math.PI);
        return constants;
    }

    public static String cos(String[] args) {
        if (args.length != 1) throw new RuntimeException("incorrect number of arguments for cos, expected 1");
        return Double.toString(Math.cos(Double.parseDouble(args[0])));
    }

    public static String sin(String[] args) {
        if (args.length != 1) throw new RuntimeException("incorrect number of arguments for sin, expected 1");
        return Double.toString(Math.sin(Double.parseDouble(args[0])));
    }

    public static String tan(String[] args) {
        if (args.length != 1) throw new RuntimeException("incorrect number of arguments for sin, expected 1");
        return Double.toString(Math.tan(Double.parseDouble(args[0])));
    }

    public static String power(String sargs) {
        Double[] args = Arrays.stream(sargs.split("\\^")).map(Double::parseDouble).toArray(Double[]::new);
        double start = args[args.length - 1];
        for (int i = args.length - 2; i > -1; --i) {
            start = Math.pow(args[i], start);
        }
        return Double.toString(start);
    }

    public static String multiplyOrDivideOrModulo(String args) {
        Matcher m = Pattern.compile("([*/%])\\s*(" + ExtensibleCalculator.doubleMatch + ")\\s*").matcher("*" + args);
        double start = 1;
        while (m.find()) {
            switch (m.group(1)) {
                case "*":
                    start *= Double.parseDouble(m.group(2));
                    break;
                case "/":
                    start /= Double.parseDouble(m.group(2));
                    break;
                case "%":
                    start %= Double.parseDouble(m.group(2));
            }
        }
        return Double.toString(start);
    }

    public static String addOrSubtract(String args) {
        Matcher m = Pattern.compile("([+-])\\s*(" + ExtensibleCalculator.doubleMatch + ")\\s*").matcher("+" + args);
        double start = 0;
        while (m.find()) {
            if (m.group(1).equals("+")) {
                start += Double.parseDouble(m.group(2));
            } else {
                start -= Double.parseDouble(m.group(2));
            }
        }
        return Double.toString(start);

    }
}
