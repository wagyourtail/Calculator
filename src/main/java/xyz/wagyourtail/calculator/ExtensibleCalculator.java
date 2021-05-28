package xyz.wagyourtail.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtensibleCalculator {
    protected static final Map<String, Function<String[], String>> functionOperations = DefaultOperations.getFunctionOperations();
    protected static final Map<String, Function<String, String>> symbolOperations = DefaultOperations.getSymbolOperations();
    protected static final Map<String, Double> constants = DefaultOperations.getConstants();
    public static final String doubleMatch = "(?:-?\\d*\\.?\\d+(?:E-?\\d+)?|NaN)";


    public static void main(String... args) {
        String input = String.join(" ", args);
        System.out.println(calculate("4 * 1 % 2 * 5"));
    }


    public static String calculate(String input) {
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
                throw new RuntimeException("failed to parse \"" + input + "\" due to an unknown operation");
            }
            input = newInput;
        }

        return input;
    }

    private static String parseFunction(String name, String combined_args) {
        if (!functionOperations.containsKey(name)) throw new RuntimeException("function \"" + name + "\" does not exist");

        //this is the only recursion and means we only go one level deep since we are parsing only "ready" functions.
        String[] args = Arrays.stream(combined_args.split(",")).map(ExtensibleCalculator::calculate).toArray(String[]::new);
        return functionOperations.get(name).apply(args);
    }

    /**
     * only use on symbol only math
     */
    private static String parseSymbols(String input) {
        while (!input.matches(doubleMatch)) {
            String newInput = input;
            for (Map.Entry<String, Function<String, String>> sym : symbolOperations.entrySet()) {
                newInput = replaceFunction(newInput, Pattern.compile(doubleMatch + "(?:\\s*" + sym.getKey() + "\\s*" + doubleMatch + ")+"), it -> sym.getValue().apply(it[0]));
            }
            if (newInput.equals(input)) {
                throw new RuntimeException("failed to parse \"" + input + "\" due to an unknown symbol");
            }
            input = newInput;
        }
        return input;
    }

    public static @NotNull String replaceFunction(@NotNull String str, @NotNull Pattern pattern, @NotNull Function<String[], String> replaceFn) {
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
