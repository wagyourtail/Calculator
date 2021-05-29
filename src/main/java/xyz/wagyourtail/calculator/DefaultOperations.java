package xyz.wagyourtail.calculator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultOperations {

    public static Map<String, FunctionWithException<String[], String>> getFunctionOperations() {
        Map<String, FunctionWithException<String[], String>> ops = new LinkedHashMap<>();
        ops.put("cos", DefaultOperations::cos);
        ops.put("sin", DefaultOperations::sin);
        ops.put("tan", DefaultOperations::tan);
        ops.put("floor", DefaultOperations::floor);
        ops.put("ceil", DefaultOperations::ceil);
        ops.put("round", DefaultOperations::round);
        return ops;
    }

    public static Map<String, FunctionWithException<String, String>> getSymbolOperations() {
        Map<String, FunctionWithException<String, String>> ops = new LinkedHashMap<>();
        ops.put("\\^", DefaultOperations::power);
        ops.put("\\*|/|%", DefaultOperations::multiplyOrDivideOrModulo);
        ops.put("\\+|-",  DefaultOperations::addOrSubtract);
        ops.put("<<|>>|\\||&", DefaultOperations::bitwiseOperators);
        return ops;
    }

    public static Map<String, Double> getConstants() {
        Map<String, Double> constants = new LinkedHashMap<>();
        constants.put("pi", Math.PI);
        return constants;
    }

    public static String cos(String[] args) throws Exceptions.FunctionParameterCountException {
        if (args.length != 1) throw new Exceptions.FunctionParameterCountException("cos", 1, args.length);
        return Double.toString(Math.cos(Double.parseDouble(args[0])));
    }

    public static String sin(String[] args) throws Exceptions.FunctionParameterCountException {
        if (args.length != 1) throw new Exceptions.FunctionParameterCountException("sin", 1, args.length);
        return Double.toString(Math.sin(Double.parseDouble(args[0])));
    }

    public static String tan(String[] args) throws Exceptions.FunctionParameterCountException {
        if (args.length != 1) throw new Exceptions.FunctionParameterCountException("tan", 1, args.length);
        return Double.toString(Math.tan(Double.parseDouble(args[0])));
    }

    public static String floor(String[] args) throws Exceptions.FunctionParameterCountException {
        if (args.length != 1) throw new Exceptions.FunctionParameterCountException("floor", 1, args.length);
        return Double.toString(Math.floor(Double.parseDouble(args[0])));
    }

    public static String ceil(String[] args) throws Exceptions.FunctionParameterCountException {
        if (args.length != 1) throw new Exceptions.FunctionParameterCountException("ceil", 1, args.length);
        return Double.toString(Math.ceil(Double.parseDouble(args[0])));
    }

    public static String round(String[] args) throws Exceptions.FunctionParameterCountException {
        if (args.length != 1) throw new Exceptions.FunctionParameterCountException("round", 1, args.length);
        return Double.toString(Math.round(Double.parseDouble(args[0])));
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
        Matcher m = Pattern.compile("(\\*|/|%)\\s*(" + ExtensibleCalculator.doubleMatch + ")\\s*").matcher("*" + args);
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
        Matcher m = Pattern.compile("(\\+|-)\\s*(" + ExtensibleCalculator.doubleMatch + ")\\s*").matcher("+" + args);
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

    public static String bitwiseOperators(String args) throws Exceptions.BitwiseCalculationException {
        //shortcut if no-op.
        if (args.matches(ExtensibleCalculator.doubleMatch)) {
            return args;
        }

        Matcher m = Pattern.compile("(<<|>>|\\||&)\\s*(" + ExtensibleCalculator.doubleMatch + ")\\s*").matcher("|" + args);
        int start = 0;
        while (m.find()) {
            double dval = Double.parseDouble(m.group(2));
            int ival = (int) dval;
            if (dval != ival) throw new Exceptions.BitwiseCalculationException(dval);
            switch (m.group(1)) {
                case ">>":
                    start >>= ival;
                    break;
                case "<<":
                    start <<= ival;
                    break;
                case "|":
                    start |= ival;
                    break;
                case "&":
                    start &= ival;
                    break;
            }
        }
        return Integer.toString(start);
    }
}
