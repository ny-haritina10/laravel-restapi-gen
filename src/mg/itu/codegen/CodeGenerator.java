package mg.itu.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import mg.itu.data.Column;
import mg.itu.data.Table;

public class CodeGenerator {

    private Table table;
    private String outputPath;
    
    public CodeGenerator(Table table, String outputPath) {
        this.table = table;
        this.outputPath = outputPath;
    }
    
    public void generateAll() throws IOException {
        generateModel();
        generateController();
        generateService();
        generateRoutes();
    }
    
    private void generateModel() throws IOException {
        String modelName = table.getModelName();
        StringBuilder modelCode = new StringBuilder();
        
        modelCode.append("<?php\n\n");
        modelCode.append("namespace App\\Models;\n\n");
        modelCode.append("use Illuminate\\Database\\Eloquent\\Factories\\HasFactory;\n");
        modelCode.append("use Illuminate\\Database\\Eloquent\\Model;\n\n");
        modelCode.append("class ").append(modelName).append(" extends Model\n");
        modelCode.append("{\n");
        modelCode.append("    use HasFactory;\n\n");
        
        // Table name
        modelCode.append("    protected $table = '").append(table.getName()).append("';\n\n");
        
        // Fillable fields
        modelCode.append("    protected $fillable = [\n");
        for (Column column : table.getColumns()) {
            if (!column.isPrimaryKey() && !column.isTimestamp()) {
                modelCode.append("        '").append(column.getName()).append("',\n");
            }
        }
        modelCode.append("    ];\n\n");
        
        // Casts
        boolean hasCasts = false;
        StringBuilder castsCode = new StringBuilder();
        castsCode.append("    protected $casts = [\n");
        
        for (Column column : table.getColumns()) {
            if (column.getPhpType().equals("boolean")) {
                castsCode.append("        '").append(column.getName()).append("' => 'boolean',\n");
                hasCasts = true;
            } else if (column.getPhpType().equals("integer")) {
                castsCode.append("        '").append(column.getName()).append("' => 'integer',\n");
                hasCasts = true;
            } else if (column.getPhpType().equals("float")) {
                castsCode.append("        '").append(column.getName()).append("' => 'float',\n");
                hasCasts = true;
            } else if (column.getPhpType().equals("json")) {
                castsCode.append("        '").append(column.getName()).append("' => 'array',\n");
                hasCasts = true;
            } else if (column.getPhpType().equals("date") || column.getPhpType().equals("datetime")) {
                castsCode.append("        '").append(column.getName()).append("' => 'datetime',\n");
                hasCasts = true;
            }
        }
        
        castsCode.append("    ];\n");
        
        if (hasCasts) {
            modelCode.append(castsCode);
        }
        
        modelCode.append("}\n");
        
        writeToFile(outputPath + File.separator + modelName + ".php", modelCode.toString());
    }
    
    private void generateController() throws IOException {
        String controllerName = table.getControllerName();
        String modelName = table.getModelName();
        String serviceName = table.getServiceName();
        String variableName = lcfirst(modelName);
        
        StringBuilder controllerCode = new StringBuilder();
        
        controllerCode.append("<?php\n\n");
        controllerCode.append("namespace App\\Http\\Controllers;\n\n");
        controllerCode.append("use App\\Models\\").append(modelName).append(";\n");
        controllerCode.append("use App\\Services\\").append(serviceName).append(";\n");
        controllerCode.append("use Illuminate\\Http\\Request;\n");
        controllerCode.append("use Illuminate\\Http\\JsonResponse;\n");
        controllerCode.append("use Illuminate\\Validation\\ValidationException;\n\n");
        
        controllerCode.append("class ").append(controllerName).append(" extends Controller\n");
        controllerCode.append("{\n");
        controllerCode.append("    protected $").append(lcfirst(serviceName)).append(";\n\n");
        
        // Constructor
        controllerCode.append("    public function __construct(").append(serviceName).append(" $").append(lcfirst(serviceName)).append(")\n");
        controllerCode.append("    {\n");
        controllerCode.append("        $this->").append(lcfirst(serviceName)).append(" = $").append(lcfirst(serviceName)).append(";\n");
        controllerCode.append("    }\n\n");
        
        // Index method
        controllerCode.append("    /**\n");
        controllerCode.append("     * Display a listing of ").append(variableName).append("s.\n");
        controllerCode.append("     *\n");
        controllerCode.append("     * @return JsonResponse\n");
        controllerCode.append("     */\n");
        controllerCode.append("    public function index(): JsonResponse\n");
        controllerCode.append("    {\n");
        controllerCode.append("        $").append(table.getName()).append(" = $this->").append(lcfirst(serviceName)).append("->getAll();\n");
        controllerCode.append("        return response()->json($").append(table.getName()).append(");\n");
        controllerCode.append("    }\n\n");
        
        // Store method
        controllerCode.append("    /**\n");
        controllerCode.append("     * Store a newly created ").append(variableName).append(".\n");
        controllerCode.append("     *\n");
        controllerCode.append("     * @param Request $request\n");
        controllerCode.append("     * @return JsonResponse\n");
        controllerCode.append("     * @throws ValidationException\n");
        controllerCode.append("     */\n");
        controllerCode.append("    public function store(Request $request): JsonResponse\n");
        controllerCode.append("    {\n");
        controllerCode.append("        $validated = $request->validate([\n");
        
        for (Column column : table.getColumns()) {
            if (!column.isPrimaryKey() && !column.isTimestamp()) {
                String rules = column.isNullable() ? "nullable" : "required";
                
                if (column.getPhpType().equals("integer")) {
                    rules += "|integer";
                } else if (column.getPhpType().equals("float")) {
                    rules += "|numeric";
                } else if (column.getPhpType().equals("boolean")) {
                    rules += "|boolean";
                } else if (column.getPhpType().equals("date")) {
                    rules += "|date";
                } else if (column.getPhpType().equals("datetime")) {
                    rules += "|date";
                } else if (column.getPhpType().equals("json")) {
                    rules += "|array";
                } else {
                    rules += "|string";
                }
                
                controllerCode.append("            '").append(column.getName()).append("' => '").append(rules).append("',\n");
            }
        }
        
        controllerCode.append("        ]);\n\n");
        controllerCode.append("        $").append(variableName).append(" = $this->").append(lcfirst(serviceName)).append("->create($validated);\n");
        controllerCode.append("        return response()->json($").append(variableName).append(", 201);\n");
        controllerCode.append("    }\n\n");
        
        // Show method
        controllerCode.append("    /**\n");
        controllerCode.append("     * Display the specified ").append(variableName).append(".\n");
        controllerCode.append("     *\n");
        controllerCode.append("     * @param int $id\n");
        controllerCode.append("     * @return JsonResponse\n");
        controllerCode.append("     */\n");
        controllerCode.append("    public function show(int $id): JsonResponse\n");
        controllerCode.append("    {\n");
        controllerCode.append("        $").append(variableName).append(" = $this->").append(lcfirst(serviceName)).append("->findById($id);\n");
        controllerCode.append("        \n");
        controllerCode.append("        if (!$").append(variableName).append(") {\n");
        controllerCode.append("            return response()->json(['message' => '").append(modelName).append(" not found'], 404);\n");
        controllerCode.append("        }\n");
        controllerCode.append("        \n");
        controllerCode.append("        return response()->json($").append(variableName).append(");\n");
        controllerCode.append("    }\n\n");
        
        // Update method
        controllerCode.append("    /**\n");
        controllerCode.append("     * Update the specified ").append(variableName).append(".\n");
        controllerCode.append("     *\n");
        controllerCode.append("     * @param Request $request\n");
        controllerCode.append("     * @param int $id\n");
        controllerCode.append("     * @return JsonResponse\n");
        controllerCode.append("     * @throws ValidationException\n");
        controllerCode.append("     */\n");
        controllerCode.append("    public function update(Request $request, int $id): JsonResponse\n");
        controllerCode.append("    {\n");
        controllerCode.append("        $").append(variableName).append(" = $this->").append(lcfirst(serviceName)).append("->findById($id);\n");
        controllerCode.append("        \n");
        controllerCode.append("        if (!$").append(variableName).append(") {\n");
        controllerCode.append("            return response()->json(['message' => '").append(modelName).append(" not found'], 404);\n");
        controllerCode.append("        }\n\n");
        
        controllerCode.append("        $validated = $request->validate([\n");
        
        for (Column column : table.getColumns()) {
            if (!column.isPrimaryKey() && !column.isTimestamp()) {
                String rules = "nullable";
                
                if (column.getPhpType().equals("integer")) {
                    rules += "|integer";
                } else if (column.getPhpType().equals("float")) {
                    rules += "|numeric";
                } else if (column.getPhpType().equals("boolean")) {
                    rules += "|boolean";
                } else if (column.getPhpType().equals("date")) {
                    rules += "|date";
                } else if (column.getPhpType().equals("datetime")) {
                    rules += "|date";
                } else if (column.getPhpType().equals("json")) {
                    rules += "|array";
                } else {
                    rules += "|string";
                }
                
                controllerCode.append("            '").append(column.getName()).append("' => '").append(rules).append("',\n");
            }
        }
        
        controllerCode.append("        ]);\n\n");
        controllerCode.append("        $updated").append(modelName).append(" = $this->").append(lcfirst(serviceName)).append("->update($id, $validated);\n");
        controllerCode.append("        return response()->json($updated").append(modelName).append(");\n");
        controllerCode.append("    }\n\n");
        
        // Delete method
        controllerCode.append("    /**\n");
        controllerCode.append("     * Remove the specified ").append(variableName).append(".\n");
        controllerCode.append("     *\n");
        controllerCode.append("     * @param int $id\n");
        controllerCode.append("     * @return JsonResponse\n");
        controllerCode.append("     */\n");
        controllerCode.append("    public function destroy(int $id): JsonResponse\n");
        controllerCode.append("    {\n");
        controllerCode.append("        $").append(variableName).append(" = $this->").append(lcfirst(serviceName)).append("->findById($id);\n");
        controllerCode.append("        \n");
        controllerCode.append("        if (!$").append(variableName).append(") {\n");
        controllerCode.append("            return response()->json(['message' => '").append(modelName).append(" not found'], 404);\n");
        controllerCode.append("        }\n\n");
        
        controllerCode.append("        $this->").append(lcfirst(serviceName)).append("->delete($id);\n");
        controllerCode.append("        return response()->json(null, 204);\n");
        controllerCode.append("    }\n");
        controllerCode.append("}\n");
        
        writeToFile(outputPath + File.separator + controllerName + ".php", controllerCode.toString());
    }
    
    private void generateService() throws IOException {
        String serviceName = table.getServiceName();
        String modelName = table.getModelName();
        String variableName = lcfirst(modelName);
        
        StringBuilder serviceCode = new StringBuilder();
        
        serviceCode.append("<?php\n\n");
        serviceCode.append("namespace App\\Services;\n\n");
        serviceCode.append("use App\\Models\\").append(modelName).append(";\n");
        serviceCode.append("use Illuminate\\Database\\Eloquent\\Collection;\n\n");
        
        serviceCode.append("class ").append(serviceName).append("\n");
        serviceCode.append("{\n");
        
        // Get all method
        serviceCode.append("    /**\n");
        serviceCode.append("     * Get all ").append(variableName).append("s.\n");
        serviceCode.append("     *\n");
        serviceCode.append("     * @return Collection\n");
        serviceCode.append("     */\n");
        serviceCode.append("    public function getAll(): Collection\n");
        serviceCode.append("    {\n");
        serviceCode.append("        return ").append(modelName).append("::all();\n");
        serviceCode.append("    }\n\n");
        
        // Find by ID method
        serviceCode.append("    /**\n");
        serviceCode.append("     * Find ").append(variableName).append(" by ID.\n");
        serviceCode.append("     *\n");
        serviceCode.append("     * @param int $id\n");
        serviceCode.append("     * @return ").append(modelName).append("|null\n");
        serviceCode.append("     */\n");
        serviceCode.append("    public function findById(int $id): ?").append(modelName).append("\n");
        serviceCode.append("    {\n");
        serviceCode.append("        return ").append(modelName).append("::find($id);\n");
        serviceCode.append("    }\n\n");
        
        // Create method
        serviceCode.append("    /**\n");
        serviceCode.append("     * Create a new ").append(variableName).append(".\n");
        serviceCode.append("     *\n");
        serviceCode.append("     * @param array $data\n");
        serviceCode.append("     * @return ").append(modelName).append("\n");
        serviceCode.append("     */\n");
        serviceCode.append("    public function create(array $data): ").append(modelName).append("\n");
        serviceCode.append("    {\n");
        serviceCode.append("        return ").append(modelName).append("::create($data);\n");
        serviceCode.append("    }\n\n");
        
        // Update method
        serviceCode.append("    /**\n");
        serviceCode.append("     * Update the specified ").append(variableName).append(".\n");
        serviceCode.append("     *\n");
        serviceCode.append("     * @param int $id\n");
        serviceCode.append("     * @param array $data\n");
        serviceCode.append("     * @return ").append(modelName).append("|null\n");
        serviceCode.append("     */\n");
        serviceCode.append("    public function update(int $id, array $data): ?").append(modelName).append("\n");
        serviceCode.append("    {\n");
        serviceCode.append("        $").append(variableName).append(" = $this->findById($id);\n");
        serviceCode.append("        \n");
        serviceCode.append("        if (!$").append(variableName).append(") {\n");
        serviceCode.append("            return null;\n");
        serviceCode.append("        }\n\n");
        serviceCode.append("        $").append(variableName).append("->update($data);\n");
        serviceCode.append("        return $").append(variableName).append("->fresh();\n");
        serviceCode.append("    }\n\n");
        
        // Delete method
        serviceCode.append("    /**\n");
        serviceCode.append("     * Delete the specified ").append(variableName).append(".\n");
        serviceCode.append("     *\n");
        serviceCode.append("     * @param int $id\n");
        serviceCode.append("     * @return bool\n");
        serviceCode.append("     */\n");
        serviceCode.append("    public function delete(int $id): bool\n");
        serviceCode.append("    {\n");
        serviceCode.append("        $").append(variableName).append(" = $this->findById($id);\n");
        serviceCode.append("        \n");
        serviceCode.append("        if (!$").append(variableName).append(") {\n");
        serviceCode.append("            return false;\n");
        serviceCode.append("        }\n\n");
        serviceCode.append("        return $").append(variableName).append("->delete();\n");
        serviceCode.append("    }\n");
        serviceCode.append("}\n");
        
        writeToFile(outputPath + File.separator + serviceName + ".php", serviceCode.toString());
    }
    
    private void generateRoutes() throws IOException {
        String modelName = table.getModelName();
        String controllerName = table.getControllerName();
        String routeName = table.getName();
        
        StringBuilder routesCode = new StringBuilder();
        
        routesCode.append("<?php\n\n");
        routesCode.append("use Illuminate\\Support\\Facades\\Route;\n");
        routesCode.append("use App\\Http\\Controllers\\").append(controllerName).append(";\n\n");
        
        routesCode.append("// Routes for ").append(modelName).append(" CRUD operations\n");
        routesCode.append("Route::apiResource('").append(routeName).append("', ").append(controllerName).append("::class);\n\n");
        
        routesCode.append("/* The above apiResource route is equivalent to:\n");
        routesCode.append("Route::get('/").append(routeName).append("', [").append(controllerName).append("::class, 'index']);\n");
        routesCode.append("Route::post('/").append(routeName).append("', [").append(controllerName).append("::class, 'store']);\n");
        routesCode.append("Route::get('/").append(routeName).append("/{id}', [").append(controllerName).append("::class, 'show']);\n");
        routesCode.append("Route::put('/").append(routeName).append("/{id}', [").append(controllerName).append("::class, 'update']);\n");
        routesCode.append("Route::delete('/").append(routeName).append("/{id}', [").append(controllerName).append("::class, 'destroy']);\n");
        routesCode.append("*/\n");
        
        writeToFile(outputPath + File.separator + routeName + "_routes.php", routesCode.toString());
    }
    
    private void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        
        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        
        // Write to file
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
    
    private String lcfirst(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toLowerCase(input.charAt(0)) + input.substring(1);
    }
}