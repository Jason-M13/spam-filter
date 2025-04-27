package csci2020u.spamprogram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpamDetector {
    private HashMap<String, Integer> trainHamFreq;
    private HashMap<String, Integer> trainSpamFreq;
    private HashMap<String, Double> probability;
    private int countSpam;
    private int countHam;
    private testHelper testHelper;

    public SpamDetector() {
        trainHamFreq = new HashMap<>();
        trainSpamFreq = new HashMap<>();
        probability = new HashMap<>();
        countSpam = 0;
        countHam = 0;
        testHelper = new testHelper(this);
    }

    // Train the model using a given directory
    public void trainModel(File dirTrain) {
        if (!dirTrain.exists() || !dirTrain.isDirectory()) {
            System.out.println("Invalid directory: " + dirTrain.getAbsolutePath());
            return;
        }

        // System.out.println("Training with directory: " + dirTrain.getAbsolutePath());

        // Process Spam folder
        File dirSpam = new File(dirTrain, "spam");
        if (dirSpam.exists() && dirSpam.isDirectory()) {
            processFolder(dirSpam, trainSpamFreq);
            countSpam += dirSpam.listFiles().length;
        } else {
            System.out.println("Spam folder not found: " + dirSpam.getAbsolutePath());
        }

        // Process Ham folders (ham, ham1, ham2, ...)
        File[] dirContents = dirTrain.listFiles();
        if (dirContents != null) {
            for (File dir : dirContents) {
                if (dir.isDirectory() && dir.getName().startsWith("ham")) {
                    processFolder(dir, trainHamFreq);
                    countHam += dir.listFiles().length;
                }
            }
        }

        // System.out.println("Training complete. Spam count: " + countSpam + ", Ham
        // count: " + countHam);
    }

    // Helper method to process spam or ham folder
    private void processFolder(File folder, HashMap<String, Integer> freqMap) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    List<String> words = parseFile(file);
                    for (String word : words) {
                        freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
    }

    // Read a file and extract words
    public List<String> parseFile(File file) {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                for (String word : line.toLowerCase().replaceAll("[^a-z]", " ").split("\\s+")) {
                    if (!word.isEmpty()) {
                        words.add(word);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    // Calculate probabilities for each word
    public void calcProbabilities() {
        for (String word : trainSpamFreq.keySet()) {
            double wordGivenSpam = (trainSpamFreq.getOrDefault(word, 0) + 1.0) / (countSpam + 1.0);
            double wordGivenHam = (trainHamFreq.getOrDefault(word, 0) + 1.0) / (countHam + 1.0);
            double spamGivenWord = wordGivenSpam / (wordGivenSpam + wordGivenHam);
            probability.put(word, spamGivenWord);
        }
    }

    public HashMap<String, Double> getProbability() {
        return probability;
    }

    public testHelper getTestHelper() {
        return testHelper;
    }
}
