import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {
    private JTextArea displayArea;
    private JLabel inputFileLabel;
    private Map<String, Integer> wordFrequency;
    private Set<String> stopWords;
    private String currentFileName;

    public TagExtractor() {
        // Initialize data structures
        wordFrequency = new TreeMap<>();
        stopWords = new TreeSet<>();

        // Setup GUI
        setTitle("Tag/Keyword Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout(10, 10));

        // Create components
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputFileLabel = new JLabel("No file selected");
        topPanel.add(inputFileLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loadFileBtn = new JButton("Load Text File");
        JButton loadStopWordsBtn = new JButton("Load Stop Words");
        JButton saveTagsBtn = new JButton("Save Tags");
        buttonPanel.add(loadFileBtn);
        buttonPanel.add(loadStopWordsBtn);
        buttonPanel.add(saveTagsBtn);

        // Text area with scroll pane
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add button listeners
        loadFileBtn.addActionListener(e -> loadTextFile());
        loadStopWordsBtn.addActionListener(e -> loadStopWords());
        saveTagsBtn.addActionListener(e -> saveTags());
    }

    private void loadTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentFileName = selectedFile.getName();
            inputFileLabel.setText("Current file: " + currentFileName);

            processTextFile(selectedFile);
            displayTags();
        }
    }

    private void loadStopWords() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File stopWordsFile = fileChooser.getSelectedFile();
            loadStopWordsFromFile(stopWordsFile);
            JOptionPane.showMessageDialog(this,
                    "Stop words loaded successfully.\nTotal stop words: " + stopWords.size());
        }
    }

    private void processTextFile(File file) {
        wordFrequency.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processLine(String line) {
        // Remove non-letter characters and convert to lowercase
        String[] words = line.replaceAll("[^a-zA-Z ]", "")
                .toLowerCase()
                .split("\\s+");

        for (String word : words) {
            if (!word.isEmpty() && !stopWords.contains(word)) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }
    }

    private void loadStopWordsFromFile(File file) {
        stopWords.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String word;
            while ((word = reader.readLine()) != null) {
                stopWords.add(word.trim().toLowerCase());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading stop words file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayTags() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tags and Frequencies for: ").append(currentFileName).append("\n\n");

        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            sb.append(String.format("%-20s: %d%n", entry.getKey(), entry.getValue()));
        }

        displayArea.setText(sb.toString());
    }

    private void saveTags() {
        if (wordFrequency.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No tags to save. Please process a text file first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Tags and Frequencies for: " + currentFileName);
                writer.println();

                for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                    writer.printf("%-20s: %d%n", entry.getKey(), entry.getValue());
                }

                JOptionPane.showMessageDialog(this, "Tags saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error saving tags: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractor extractor = new TagExtractor();
            extractor.setVisible(true);
        });
    }
}