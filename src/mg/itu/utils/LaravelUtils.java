package mg.itu.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public /**
* Helper methods for the Laravel CRUD Generator
*/
class LaravelUtils {

   /**
    * Map PostgreSQL data types to their corresponding PHP validation rules
    * 
    * @param postgresType The PostgreSQL data type
    * @param isRequired Whether the field is required
    * @return The appropriate Laravel validation rule
    */
   public static String getValidationRule(String postgresType, boolean isRequired) {
       String rule = isRequired ? "required" : "nullable";
       
       switch (postgresType.toLowerCase()) {
           case "int":
           case "integer":
           case "smallint":
           case "bigint":
           case "serial":
           case "bigserial":
               return rule + "|integer";
           case "decimal":
           case "numeric":
           case "real":
           case "double":
           case "float":
               return rule + "|numeric";
           case "boolean":
               return rule + "|boolean";
           case "date":
               return rule + "|date";
           case "timestamp":
           case "timestamptz":
               return rule + "|date";
           case "time":
               return rule + "|date_format:H:i:s";
           case "json":
           case "jsonb":
               return rule + "|array";
           case "uuid":
               return rule + "|uuid";
           case "inet":
           case "cidr":
               return rule + "|ip";
           case "email":
               return rule + "|email";
           case "url":
               return rule + "|url";
           case "char":
           case "varchar":
           case "text":
           default:
               return rule + "|string";
       }
   }
   
   /**
    * Generate directory structure for Laravel files
    * 
    * @param baseOutputPath The base output directory
    * @return Map of paths for different file types
    */
   public static Map<String, String> createDirectoryStructure(String baseOutputPath) {
       Map<String, String> paths = new HashMap<>();
       
       // Define paths
       paths.put("models", baseOutputPath + File.separator + "Models");
       paths.put("controllers", baseOutputPath + File.separator + "Http" + File.separator + "Controllers");
       paths.put("services", baseOutputPath + File.separator + "Services");
       paths.put("routes", baseOutputPath + File.separator + "routes");
       
       // Create directories
       for (String path : paths.values()) {
           File directory = new File(path);
           directory.mkdirs();
       }
       
       return paths;
   }
   
       /**
     * Check if a table schema is valid for PostgreSQL
     *
     * @param schema The table schema to validate
     * @return True if the schema is valid, false otherwise
     */
    public static boolean checkTableSchema(Map<String, String> schema) {
        if (schema == null || schema.isEmpty()) {
            return false;
        }
        
        for (Map.Entry<String, String> entry : schema.entrySet()) {
            String columnName = entry.getKey();
            String columnType = entry.getValue();
            
            if (columnName == null || columnName.trim().isEmpty() || columnType == null || columnType.trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Generate a Laravel migration file content based on the given schema
     *
     * @param tableName The name of the table
     * @param schema The table schema (column name -> column type)
     * @return The Laravel migration file content as a string
     */
    public static String generateMigration(String tableName, Map<String, String> schema) {
        if (!checkTableSchema(schema)) {
            throw new IllegalArgumentException("Invalid schema provided");
        }
        
        StringBuilder migration = new StringBuilder();
        migration.append("<?php\n\n");
        migration.append("use Illuminate\\Database\\Migrations\\Migration;\n");
        migration.append("use Illuminate\\Database\\Schema\\Blueprint;\n");
        migration.append("use Illuminate\\Support\\Facades\\Schema;\n\n");
        migration.append("return new class extends Migration {\n");
        migration.append("    public function up() {\n");
        migration.append("        Schema::create(\"" + tableName + "\", function (Blueprint $table) {\n");
        migration.append("            $table->id();\n");
        
        for (Map.Entry<String, String> entry : schema.entrySet()) {
            migration.append("            $table->" + mapToLaravelColumnType(entry.getValue()) + "(\"" + entry.getKey() + "\");\n");
        }
        
        migration.append("            $table->timestamps();\n");
        migration.append("        });\n");
        migration.append("    }\n\n");
        migration.append("    public function down() {\n");
        migration.append("        Schema::dropIfExists(\"" + tableName + "\");\n");
        migration.append("    }\n");
        migration.append("};");
        
        return migration.toString();
    }
    
    /**
     * Map PostgreSQL data types to Laravel migration column types
     *
     * @param postgresType The PostgreSQL data type
     * @return The corresponding Laravel column type
     */
    private static String mapToLaravelColumnType(String postgresType) {
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
            case "float":
                return "double";
            case "boolean":
                return "boolean";
            case "date":
                return "date";
            case "timestamp":
            case "timestamptz":
                return "timestamp";
            case "time":
                return "time";
            case "json":
            case "jsonb":
                return "json";
            case "uuid":
                return "uuid";
            case "inet":
            case "cidr":
                return "string";
            case "char":
            case "varchar":
            case "text":
            default:
                return "string";
        }
    }
}