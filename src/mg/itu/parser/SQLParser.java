package mg.itu.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {
    /**
     * Parse a PostgreSQL schema CREATE TABLE statement to extract column information
     * 
     * @param createTableStatement The CREATE TABLE statement
     * @return A list of columns with their types and constraints
     */
    public static List<Map<String, Object>> parseCreateTable(String createTableStatement) {
        List<Map<String, Object>> columns = new ArrayList<>();
        
        // Extract column definitions between parentheses
        Pattern pattern = Pattern.compile("\\((.+)\\)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(createTableStatement);
        
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid CREATE TABLE statement: missing column definitions");
        }
        
        String columnDefinitions = matcher.group(1);
        
        // Split column definitions by commas, but be careful with commas inside parentheses
        List<String> columnDefs = new ArrayList<>();
        int parenthesesCount = 0;
        StringBuilder currentColumn = new StringBuilder();
        
        for (char c : columnDefinitions.toCharArray()) {
            if (c == '(') {
                parenthesesCount++;
            } else if (c == ')') {
                parenthesesCount--;
            }
            
            if (c == ',' && parenthesesCount == 0) {
                columnDefs.add(currentColumn.toString().trim());
                currentColumn = new StringBuilder();
            } else {
                currentColumn.append(c);
            }
        }
        
        if (currentColumn.length() > 0) {
            columnDefs.add(currentColumn.toString().trim());
        }
        
        // Process each column definition
        for (String colDef : columnDefs) {
            // Skip table constraints
            if (colDef.toLowerCase().startsWith("constraint") ||
                colDef.toLowerCase().startsWith("primary key") ||
                colDef.toLowerCase().startsWith("foreign key") ||
                colDef.toLowerCase().startsWith("unique")) {
                continue;
            }
            
            Map<String, Object> column = parseColumnDefinition(colDef);
            if (column != null) {
                columns.add(column);
            }
        }
        
        return columns;
    }
    
    private static Map<String, Object> parseColumnDefinition(String columnDef) {
        String[] parts = columnDef.trim().split("\\s+", 2);
        
        if (parts.length < 2) {
            return null;
        }
        
        String name = parts[0].replace("\"", "").trim();
        String typeWithConstraints = parts[1].trim();
        
        // Extract data type
        String dataType;
        if (typeWithConstraints.contains(" ")) {
            dataType = typeWithConstraints.split(" ")[0].trim();
        } else {
            dataType = typeWithConstraints;
        }
        
        // Clean up data type (remove size, etc.)
        if (dataType.contains("(")) {
            dataType = dataType.substring(0, dataType.indexOf("("));
        }
        
        Map<String, Object> column = new HashMap<>();
        column.put("name", name);
        column.put("type", dataType.toLowerCase());
        column.put("nullable", !columnDef.toLowerCase().contains("not null"));
        column.put("primary", columnDef.toLowerCase().contains("primary key"));
        
        return column;
    }
}   