package xyz.wagyourtail.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtensibleCalculator {
    protected static final Map<String, FunctionWithException<String[], String>> functionOperations = DefaultOperations.getFunctionOperations();
    protected static final Map<String, FunctionWithException<String, String>> symbolOperations = DefaultOperations.getSymbolOperations();
    protected static final Map<String, Double> constants = DefaultOperations.getConstants();
    public static final String doubleMatch = "(?:-?\\d*\\.?\\d+(?:E-?\\d+)?|NaN)";


    public static void main(String... args) {
        String input = String.join(" ", args);
        try {
            System.out.println(calculate(input));
        } catch (Exceptions.CalculationException e) {
            e.printStackTrace();
        }
    }


    public static String calculate(String input) throws Exceptions.CalculationException {
        //prepare input
        input = "(" + input + ")";
        for (Map.Entry<String, Double> constant : constants.entrySet()) {
            input = input.replaceAll("\\b" + constant.getKey() + "\\b", constant.getValue().toString());
        }

        while (!input.matches(doubleMatch)) {
            String newInput = input;

            //parse ready* functions
            newInput = replaceFunction(newInput, Pattern.compile("([a-zA-Z]\\w*)\\s*\\(([^()]+)\\)"), it -> parseFunction(it[1], it[2]));

            //remove excess parenthesis
            newInput = replaceFunction(newInput, Pattern.compile("(\\(+)\\s*(" + doubleMatch +")\\s*(\\)+)"), it -> {
                if (it[1].length() < it[3].length()) {
                    return it[2] + it[3].substring(it[1].length());
                } else if (it[3].length() < it[1].length()) {
                    return it[1].substring(it[3].length()) + it[2];
                }
                return it[2];
            });

            //parse ready* symbol groups
            newInput = replaceFunction(newInput, Pattern.compile("\\(([^()]+)\\)"), it -> parseSymbols(it[1]));

            // *ready means doesn't have any nesting (grouping symbols/functions) inside that still need to be parsed.

            if (newInput.equals(input)) {
                throw new Exceptions.UnknownOperationException(input);
            }
            input = newInput;
        }

        return input;
    }

    private static String parseFunction(String name, String combined_args) throws Exceptions.CalculationException {
        if (!functionOperations.containsKey(name)) throw new Exceptions.UnknownFunctionException(name);

        //this is the only recursion and means we only go one level deep since we are parsing only "ready" functions.
        String[] args = combined_args.split(",");
        for (int i = 0; i < args.length; ++i) {
            args[i] = calculate(args[i]);
        }

        return functionOperations.get(name).apply(args);
    }

    /**
     * only use on symbol only math
     */
    private static String parseSymbols(String input) throws Exceptions.CalculationException {
        for (Map.Entry<String, FunctionWithException<String, String>> sym : symbolOperations.entrySet()) {
            input = reduceSymbol(input, sym.getKey(), sym.getValue());
        }
        if (!input.matches(doubleMatch)) throw new Exceptions.UnknownSymbolException(input);
        return input;
    }

    private static String reduceSymbol(String input, String symbolMatch, FunctionWithException<String, String> replaceFunction) throws Exceptions.CalculationException {
        Matcher m = Pattern.compile(doubleMatch + "?\\s*(?:^|" + symbolMatch + ")\\s*" + doubleMatch).matcher(input);
        StringBuilder prevMatch = new StringBuilder();
        int prevMatchStart = 0;
        int prevMatchEnd = 0;

        int offset = 0;
        while (m.find()) {
            if (m.start() == prevMatchEnd) {
                prevMatch.append(m.group(0));
                prevMatchEnd = m.end();
            } else {
                String replacement = replaceFunction.apply(prevMatch.toString());
                input = input.substring(0, prevMatchStart + offset) + replacement + input.substring(prevMatchEnd + offset);
                offset += replacement.length() - (prevMatchEnd - prevMatchStart);
                prevMatchStart = m.start();
                prevMatchEnd = m.end();
                prevMatch = new StringBuilder(m.group(0));
            }
        }
        if (!prevMatch.toString().equals("")) {
            String replacement = replaceFunction.apply(prevMatch.toString());
            input = input.substring(0, prevMatchStart + offset) + replacement + input.substring(prevMatchEnd + offset);
        }
        return input;
    }

    public static @NotNull String replaceFunction(@NotNull String str, @NotNull Pattern pattern, @NotNull FunctionWithException<String[], String> replaceFn) throws Exceptions.CalculationException {
        Matcher m = pattern.matcher(str);
        int offset = 0;
        while (m.find()) {
            String[] args = new String[m.groupCount() + 1];
            for (int i = 0; i < args.length; ++i) {
                args[i] = m.group(i);
            }
            String replacement = replaceFn.apply(args);
            int len = m.end() - m.start();
            str = str.substring(0, m.start() + offset) + replacement + str.substring(m.end() + offset);
            offset = replacement.length() - len;
        }
        return str;
    }

}
