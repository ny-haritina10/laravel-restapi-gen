package mg.itu.utils;

public class Utils {
    
    public static String toSingular(String plural) {
        // Simple singularization logic - can be expanded
        if (plural.endsWith("ies")) {
            return plural.substring(0, plural.length() - 3) + "y";
        } else if (plural.endsWith("s") && !plural.endsWith("ss")) {
            return plural.substring(0, plural.length() - 1);
        }
        return plural;
    }
    
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : input.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    public static String toCamelCase(String input) {
        String pascal = toPascalCase(input);
        if (pascal.isEmpty()) {
            return pascal;
        }
        return Character.toLowerCase(pascal.charAt(0)) + pascal.substring(1);
    }
}
