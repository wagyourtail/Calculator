package xyz.wagyourtail.calculator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultOperations {

    public static Map<String, Function<String, String>> getFunctionOperations() {
        Map<String, Function<String, String>> ops = new LinkedHashMap<>();

        return ops;
    }

    public static Map<String, Function<String, String>> getSymbolOperations() {
        Map<String, Function<String, String>> ops = new LinkedHashMap<>();
        ops.put("\\^", DefaultOperations::power);
        ops.put("[*/]", DefaultOperations::multiplyOrDivide);
        ops.put("[+-]",  DefaultOperations::addOrSubtract);
        return ops;
    }

    public static String power(String sargs) {
        Double[] args = Arrays.stream(sargs.split("\\^")).map(Double::parseDouble).toArray(Double[]::new);
        double start = args[args.length - 1];
        for (int i = args.length - 2; i > -1; --i) {
            start = Math.pow(args[i], start);
        }
        return Double.toString(start);
    }

    public static String multiplyOrDivide(String args) {
        Matcher m = Pattern.compile("([*/])\\s*(" + ExtensibleCalculator.doubleMatch + ")\\s*").matcher("*" + args);
        double start = 1;
        while (m.find()) {
            if (m.group(1).equals("*")) {
                start *= Double.parseDouble(m.group(2));
            } else {
                start /= Double.parseDouble(m.group(2));
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
