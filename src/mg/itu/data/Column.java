package mg.itu.data;

public class Column {
    
    private String name;
    private String dbType;
    private String phpType;
    private boolean primaryKey;
    private boolean nullable;
    
    public Column(String name, String dbType, String phpType, boolean primaryKey, boolean nullable) {
        this.name = name;
        this.dbType = dbType;
        this.phpType = phpType;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDbType() {
        return dbType;
    }
    
    public String getPhpType() {
        return phpType;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    
    public boolean isNullable() {
        return nullable;
    }
    
    public String getCamelCaseName() {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                if (i == 0) {
                    result.append(Character.toLowerCase(c));
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
    
    public boolean isTimestamp() {
        return name.equals("created_at") || name.equals("updated_at") || name.equals("deleted_at");
    }
}