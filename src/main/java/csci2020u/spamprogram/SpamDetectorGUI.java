package csci2020u.spamprogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SpamDetectorGUI extends JFrame {
    private SpamDetector spamDetector;
    private JTable resultTable;
    private JLabel accuracyLabel;
    private JLabel precisionLabel;

    public SpamDetectorGUI() {
        setTitle("Spam Detector");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background color for the frame
        getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        // Panel for selecting directory
        JPanel selection = new JPanel();
        JButton selectDir = new JButton("Select Directory");
        selection.add(selectDir);
        selection.setBackground(new Color(240, 240, 240)); // Slightly different shade for panel
        add(selection, BorderLayout.NORTH);

        // Table for displaying classification results
        String[] col = { "File", "Actual Class", "Spam Probability" };
        DefaultTableModel table = new DefaultTableModel(col, 0);
        resultTable = new JTable(table);
        resultTable.setBackground(new Color(255, 255, 255)); // White background for the table
        resultTable.setGridColor(new Color(200, 200, 200)); // Light grid lines for table
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Panel for displaying accuracy and precision
        JPanel stats = new JPanel();
        stats.setBackground(new Color(240, 240, 240)); // Same as selection panel
        accuracyLabel = new JLabel("Accuracy: ");
        precisionLabel = new JLabel("Precision: ");
        stats.add(accuracyLabel);
        stats.add(precisionLabel);
        add(stats, BorderLayout.SOUTH);

        // Event listener for directory selection
        selectDir.addActionListener(e -> selectDirectory());
        setVisible(true);

        Font customFont = new Font("Courier New", Font.BOLD, 14); // Font for buttons, labels, and table

        // Set font and color for labels
        accuracyLabel.setFont(customFont);
        precisionLabel.setFont(customFont);
        accuracyLabel.setForeground(new Color(0, 102, 204)); // Blue text for accuracy
        precisionLabel.setForeground(new Color(0, 102, 204)); // Blue text for precision

        // Set font for buttons
        selectDir.setFont(customFont);
        selectDir.setBackground(new Color(60, 179, 113)); // Green button
        selectDir.setForeground(Color.WHITE); // White text for button

        // Set font for table headers
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        resultTable.getTableHeader().setBackground(new Color(70, 130, 180)); // Blue header background
        resultTable.getTableHeader().setForeground(Color.WHITE); // White text for headers

        // Set font for table rows
        resultTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultTable.setRowHeight(20);
    }

    /**
     * Opens a file chooser dialog to allow the user to select a directory.
     * Validates the directory structure and processes emails using the spam
     * detector.
     */
    private void selectDirectory() {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setCurrentDirectory(new File("."));
        directoryChooser.setDialogTitle("Select the data directory containing test/train with spam/ham folders");

        // Check if user selected a valid directory
        if (directoryChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDir = directoryChooser.getSelectedFile();

            // Check if "spam" and "ham" folders exist in the directory
            boolean hasSpamFolder = new File(selectedDir, "spam").exists();
            boolean hasHamFolder = false;

            // Search for any "ham" folder in the directory
            File[] contents = selectedDir.listFiles();
            if (contents != null) {
                for (File file : contents) {
                    if (file.isDirectory() && file.getName().startsWith("ham")) {
                        hasHamFolder = true;
                        break;
                    }
                }
            }

            // If spam/ham folders are not found, check inside a "train" subdirectory
            if (!hasSpamFolder || !hasHamFolder) {
                File trainDir = new File(selectedDir, "train");
                if (trainDir.exists() && trainDir.isDirectory()) {
                    selectedDir = trainDir;
                    hasSpamFolder = new File(selectedDir, "spam").exists();
                    hasHamFolder = false;
                    contents = selectedDir.listFiles();
                    if (contents != null) {
                        for (File file : contents) {
                            if (file.isDirectory() && file.getName().startsWith("ham")) {
                                hasHamFolder = true;
                                break;
                            }
                        }
                    }
                }
            }

            // Show an error message if directory structure is invalid
            if (!hasSpamFolder || !hasHamFolder) {
                JOptionPane.showMessageDialog(this, "Invalid directory structure.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Locate the "test" directory (either next to "train" or inside selected
            // directory)
            File testDir = selectedDir.getName().equals("train") ? new File(selectedDir.getParentFile(), "test")
                    : new File(selectedDir, "test");

            // Show an error message if the test directory is missing
            if (!testDir.exists() || !testDir.isDirectory()) {
                JOptionPane.showMessageDialog(this, "Test directory not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Train the spam detector using emails from the selected directory
            spamDetector = new SpamDetector();
            spamDetector.trainModel(selectedDir);
            spamDetector.calcProbabilities();

            // Classify emails in the test set and display results
            List<TestFile> results = spamDetector.getTestHelper().testModel(testDir);
            displayResults(results);
        }
    }

    /**
     * Displays classification results in the table and calculates accuracy and
     * precision.
     * 
     * @param results List of classified emails with their spam probability.
     */
    private void displayResults(List<TestFile> results) {
        DefaultTableModel tableModel = (DefaultTableModel) resultTable.getModel();
        tableModel.setRowCount(0);

        double correct = 0, total = results.size();
        int truePos = 0, falsePos = 0;

        // Iterate through classification results
        for (TestFile file : results) {
            boolean isSpam = file.getSpamProbability() > 0.4; // Threshold for spam classification
            boolean actualSpam = file.getActualClass().equals("spam");

            if (isSpam && actualSpam)
                truePos++;
            if (isSpam && !actualSpam)
                falsePos++;
            if ((isSpam && actualSpam) || (!isSpam && !actualSpam))
                correct++;

            // Add result to the table
            tableModel.addRow(new Object[] {
                    file.getFilename(),
                    file.getActualClass(),
                    String.format("%.5f", file.getSpamProbability())
            });
        }

        // Calculate accuracy and precision
        double accuracy = correct / total;
        double precision = (truePos + falsePos) == 0 ? 0 : (double) truePos / (truePos + falsePos);

        // Update GUI labels
        accuracyLabel.setText("Accuracy: " + String.format("%.5f", accuracy));
        precisionLabel.setText("Precision: " + String.format("%.5f", precision));
    }

    /**
     * Main method to run the application.
     */
    public static void main(String[] args) {
        new SpamDetectorGUI();
    }
}
