package mg.itu.parser;

import java.util.ArrayList;
import java.util.List;

import mg.itu.data.Column;
import mg.itu.data.Table;

public class SchemaParser {
    public static Table parseSchema(String schema) {
        // Simple parser for PostgreSQL CREATE TABLE statements
        schema = schema.trim();
        
        // Extract table name
        String tableName = extractTableName(schema);
        
        // Extract column definitions
        List<Column> columns = extractColumns(schema);
        
        return new Table(tableName, columns);
    }
    
    private static String extractTableName(String schema) {
        schema = schema.toLowerCase();
        int createTableIndex = schema.indexOf("create table");
    
        if (createTableIndex == -1) {
            throw new IllegalArgumentException("Invalid schema: missing CREATE TABLE statement");
        }
    
        int startNameIndex = schema.indexOf(" ", createTableIndex + 12) + 1;
        int endNameIndex = schema.indexOf("(", startNameIndex); 
    
        if (endNameIndex == -1) {
            throw new IllegalArgumentException("Invalid schema: missing '(' after table name");
        }
    
        String tableName = schema.substring(startNameIndex, endNameIndex).trim(); // Apply trim here
    
        // Remove schema prefix if it exists
        if (tableName.contains(".")) {
            tableName = tableName.substring(tableName.indexOf(".") + 1);
        }
    
        return tableName;
    }    
    
    private static List<Column> extractColumns(String schema) {
        // Find the column definitions between parentheses
        int startIndex = schema.indexOf("(");
        int endIndex = schema.lastIndexOf(")");
        
        if (startIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("Invalid schema: missing column definitions");
        }
        
        String columnDefs = schema.substring(startIndex + 1, endIndex).trim();
        
        // Split column definitions
        List<String> columnStrings = new ArrayList<>();
        int parenthesesCount = 0;
        StringBuilder currentColumn = new StringBuilder();
        
        for (char c : columnDefs.toCharArray()) {
            if (c == '(') parenthesesCount++;
            else if (c == ')') parenthesesCount--;
            
            if (c == ',' && parenthesesCount == 0) {
                columnStrings.add(currentColumn.toString().trim());
                currentColumn = new StringBuilder();
            } else {
                currentColumn.append(c);
            }
        }
        
        if (currentColumn.length() > 0) {
            columnStrings.add(currentColumn.toString().trim());
        }
        
        // Process each column definition
        List<Column> columns = new ArrayList<>();
        for (String colDef : columnStrings) {
            // Skip certain constraints
            if (colDef.toLowerCase().startsWith("constraint") ||
            colDef.toLowerCase().startsWith("primary key") ||
            colDef.toLowerCase().startsWith("unique")) {
            continue;
            }

            // Process foreign key constraints
            if (colDef.toLowerCase().startsWith("foreign key")) {
            processForeignKeyConstraint(colDef, columns);
            continue;
            }
                        
            Column column = parseColumnDefinition(colDef);
            if (column != null) {
                columns.add(column);
            }
        }
        
        return columns;
    }

    private static void processForeignKeyConstraint(String constraint, List<Column> columns) {
        // Extract column name: FOREIGN KEY (column_name) REFERENCES target_table(target_column)
        int startCol = constraint.indexOf("(");
        int endCol = constraint.indexOf(")");
        if (startCol == -1 || endCol == -1) return;
        
        String columnName = constraint.substring(startCol + 1, endCol).trim();
        
        // Extract references
        int referencesIdx = constraint.toLowerCase().indexOf("references");
        if (referencesIdx == -1) return;
        
        String references = constraint.substring(referencesIdx + "references".length()).trim();
        
        // Parse target table and column
        int targetTableEnd = references.indexOf("(");
        if (targetTableEnd == -1) return;
        
        String targetTable = references.substring(0, targetTableEnd).trim();
        
        int targetColStart = references.indexOf("(");
        int targetColEnd = references.indexOf(")", targetColStart);
        if (targetColStart == -1 || targetColEnd == -1) return;
        
        String targetColumn = references.substring(targetColStart + 1, targetColEnd).trim();
        
        // Update the corresponding column
        for (Column column : columns) {
            if (column.getName().equals(columnName)) {
                Column updatedColumn = new Column(
                    column.getName(), 
                    column.getDbType(), 
                    column.getPhpType(), 
                    column.isPrimaryKey(), 
                    column.isNullable(),
                    true,  // is foreign key
                    targetTable,
                    targetColumn
                );
                
                // Replace the column in the list
                columns.set(columns.indexOf(column), updatedColumn);
                break;
            }
        }
    }
    
    private static Column parseColumnDefinition(String colDef) {
        String[] parts = colDef.trim().split("\\s+", 2);
        
        if (parts.length < 2) {
            return null;
        }
        
        String name = parts[0].replace("\"", "").trim();
        String dataTypeWithConstraints = parts[1].trim();
        
        // Extract data type
        String dataType;
        if (dataTypeWithConstraints.contains(" ")) {
            dataType = dataTypeWithConstraints.split(" ")[0].trim();
        } else {
            dataType = dataTypeWithConstraints;
        }
        
        // Standardize data type
        dataType = dataType.toLowerCase();
        if (dataType.contains("(")) {
            dataType = dataType.substring(0, dataType.indexOf("("));
        }
        
        // Determine if it's a primary key
        boolean isPrimaryKey = colDef.toLowerCase().contains("primary key");
        
        // Determine if it's nullable
        boolean isNullable = !colDef.toLowerCase().contains("not null");
        
        // Check for inline REFERENCES
        boolean isForeignKey = false;
        String referencesTable = null;
        String referencesColumn = null;
        
        // Look for the REFERENCES keyword
        int referencesIdx = colDef.toUpperCase().indexOf("REFERENCES");
        if (referencesIdx != -1) {
            isForeignKey = true;
            String referencesPart = colDef.substring(referencesIdx + "REFERENCES".length()).trim();
            
            // Parse table and column
            int openParenIdx = referencesPart.indexOf('(');
            int closeParenIdx = referencesPart.indexOf(')', openParenIdx);
            
            if (openParenIdx != -1 && closeParenIdx != -1) {
                referencesTable = referencesPart.substring(0, openParenIdx).trim();
                referencesColumn = referencesPart.substring(openParenIdx + 1, closeParenIdx).trim();
            }
        }
        
        // Map PostgreSQL types to PHP/Laravel types
        String phpType = mapPostgresToPhpType(dataType);
        
        return new Column(name, dataType, phpType, isPrimaryKey, isNullable, isForeignKey, referencesTable, referencesColumn);
    }
        
    private static String mapPostgresToPhpType(String postgresType) {
        switch (postgresType.toLowerCase()) {
            case "int":
            case "integer":
            case "smallint":
            case "bigint":
            case "serial":
            case "bigserial":
                return "integer";
            case "decimal":
            case "numeric":
            case "real":
            case "double":
                return "float";
            case "boolean":
                return "boolean";
            case "date":
                return "date";
            case "timestamp":
            case "timestamptz":
                return "datetime";
            case "time":
                return "time";
            case "json":
            case "jsonb":
                return "json";
            case "char":
            case "varchar":
            case "text":
                return "string";
            default:
                return "string"; // Default to string for unknown types
        }
    }
}