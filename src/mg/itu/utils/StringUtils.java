package mg.itu.utils;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {
    /**
     * Convert a string to singular form (basic implementation)
     * 
     * @param plural The plural form of the word
     * @return The singular form of the word
     */
    public static String toSingular(String plural) {
        if (plural == null || plural.isEmpty()) {
            return plural;
        }
        
        // Common irregular plurals
        Map<String, String> irregulars = new HashMap<>();
        irregulars.put("children", "child");
        irregulars.put("people", "person");
        irregulars.put("men", "man");
        irregulars.put("women", "woman");
        irregulars.put("feet", "foot");
        irregulars.put("teeth", "tooth");
        
        if (irregulars.containsKey(plural.toLowerCase())) {
            return irregulars.get(plural.toLowerCase());
        }
        
        // Common rules for English plurals
        if (plural.endsWith("ies")) {
            return plural.substring(0, plural.length() - 3) + "y";
        } else if (plural.endsWith("ses") || plural.endsWith("zes") || plural.endsWith("xes") || plural.endsWith("ches") || plural.endsWith("shes")) {
            return plural.substring(0, plural.length() - 2);
        } else if (plural.endsWith("s") && !plural.endsWith("ss") && !plural.endsWith("us") && !plural.endsWith("is")) {
            return plural.substring(0, plural.length() - 1);
        }
        
        return plural;
    }
    
    /**
     * Convert a string to PascalCase
     * 
     * @param input The input string
     * @return The string in PascalCase
     */
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Replace non-alphanumeric characters with spaces
        String normalized = input.replaceAll("[^a-zA-Z0-9]", " ");
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : normalized.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Convert a string to camelCase
     * 
     * @param input The input string
     * @return The string in camelCase
     */
    public static String toCamelCase(String input) {
        String pascalCase = toPascalCase(input);
        
        if (pascalCase == null || pascalCase.isEmpty()) {
            return pascalCase;
        }
        
        return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
    }
    
    /**
     * Convert a string to snake_case
     * 
     * @param input The input string
     * @return The string in snake_case
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Replace non-alphanumeric characters with spaces
        String normalized = input.replaceAll("[^a-zA-Z0-9]", " ");
        
        StringBuilder result = new StringBuilder();
        boolean wasSpace = false;
        
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            
            if (Character.isWhitespace(c)) {
                wasSpace = true;
                continue;
            }
            
            if (wasSpace) {
                if (result.length() > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
                wasSpace = false;
            } else if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(normalized.charAt(i - 1))) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
}