package mg.itu.frame;

import javax.swing.*;

import mg.itu.codegen.CodeGenerator;
import mg.itu.data.Table;
import mg.itu.parser.SchemaParser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LaravelCrudGenerator extends JFrame {
    
    private JTextArea schemaInput;
    private JTextField outputPathField;
    private JButton generateButton;
    private JButton browseButton;

    public LaravelCrudGenerator() {
        setTitle("Laravel CRUD Generator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        addEventListeners();
    }

    private void initComponents() {
        schemaInput = new JTextArea();
        schemaInput.setLineWrap(true);
        schemaInput.setWrapStyleWord(true);
        schemaInput.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        outputPathField = new JTextField();
        generateButton = new JButton("Generate CRUD");
        browseButton = new JButton("Browse...");
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel schemaLabel = new JLabel("PostgreSQL Table Schema:");
        JLabel exampleLabel = new JLabel("<html>Example: CREATE TABLE users (id SERIAL PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), created_at TIMESTAMP, updated_at TIMESTAMP);</html>");
        exampleLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        
        JPanel schemaPanel = new JPanel(new BorderLayout(5, 5));
        schemaPanel.add(schemaLabel, BorderLayout.NORTH);
        schemaPanel.add(new JScrollPane(schemaInput), BorderLayout.CENTER);
        schemaPanel.add(exampleLabel, BorderLayout.SOUTH);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(new JLabel("Output Path:"), BorderLayout.NORTH);
        outputPanel.add(outputPathField, BorderLayout.CENTER);
        outputPanel.add(browseButton, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(generateButton);

        mainPanel.add(schemaPanel, BorderLayout.CENTER);
        mainPanel.add(outputPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Adjust the layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(schemaPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(schemaPanel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(outputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    private void addEventListeners() {
        browseButton.addActionListener(this::browseButtonClicked);
        generateButton.addActionListener(this::generateButtonClicked);
    }

    private void browseButtonClicked(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(this);
        
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            outputPathField.setText(file.getAbsolutePath());
        }
    }

    private void generateButtonClicked(ActionEvent e) {
        String schema = schemaInput.getText().trim();
        String outputPath = outputPathField.getText().trim();
        
        if (schema.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a table schema.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (outputPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an output directory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Table table = SchemaParser.parseSchema(schema);
            CodeGenerator generator = new CodeGenerator(table, outputPath);
            generator.generateAll();
            JOptionPane.showMessageDialog(this, "CRUD files generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}