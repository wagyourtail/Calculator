package xyz.wagyourtail.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtensibleCalculator {
    protected static final Map<String, Function<String, String>> functionOperations = DefaultOperations.getFunctionOperations();
    protected static final Map<String, Function<String, String>> symbolOperations = DefaultOperations.getSymbolOperations();
    public static final String doubleMatch = "(?:-?\\d*\\.?\\d+|NaN)";


    public static void main(String... args) {
        String input = String.join(" ", args);
        System.out.println(calculate(input));
    }


    public static String calculate(String input) {
        //prepare input
        input = "(" + input + ")";

        while (!input.matches(doubleMatch)) {
            //remove excess parenthesis
            input = replaceFunction(input, Pattern.compile("(\\(+)\\s*(" + doubleMatch +")\\s*(\\)+)"), it -> {
                if (it[1].length() < it[3].length()) {
                    return it[2] + it[3].substring(it[1].length());
                } else if (it[3].length() < it[1].length()) {
                    return it[1].substring(it[3].length()) + it[2];
                }
                return it[2];
            });

            //parse ready symbol groups
            input = replaceFunction(input, Pattern.compile("\\(([^()]+)\\)"), it -> parseSymbols(it[1]));

            //TODO: functions
        }

        return input;
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
