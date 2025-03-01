package mg.itu.data;

import java.util.List;

public class Table {
    private String name;
    private List<Column> columns;
    
    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Column> getColumns() {
        return columns;
    }
    
    public String getModelName() {
        return toPascalCase(toSingular(name));
    }
    
    public String getControllerName() {
        return getModelName() + "Controller";
    }
    
    public String getServiceName() {
        return getModelName() + "Service";
    }
    
    public Column getPrimaryKeyColumn() {
        for (Column column : columns) {
            if (column.isPrimaryKey()) {
                return column;
            }
        }
        // Default to "id" if no primary key is found
        return new Column("id", "integer", "integer", true, false, false, null, null);
    }
    
    private String toSingular(String plural) {
        // Simple singularization logic - can be expanded
        if (plural.endsWith("ies")) {
            return plural.substring(0, plural.length() - 3) + "y";
        } else if (plural.endsWith("s") && !plural.endsWith("ss")) {
            return plural.substring(0, plural.length() - 1);
        }
        return plural;
    }
    
    private String toPascalCase(String input) {
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
}