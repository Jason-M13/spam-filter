package csci2020u.spamprogram;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class testHelper {
    private SpamDetector spamDetector;

    // Constructor
    public testHelper(SpamDetector spamDetector) {
        this.spamDetector = spamDetector;
    }

    // Classifies emails in the test folder
    public List<TestFile> testModel(File dirTest) {
        List<TestFile> testResults = new ArrayList<>();

        // Process spam and ham test emails
        testResults.addAll(classifyEmail(new File(dirTest, "spam"), "spam"));
        testResults.addAll(classifyEmail(new File(dirTest, "ham"), "ham"));

        return testResults;
    }

    // Classifies emails using Naive Bayes formula(the formula given in the ReadMe)
    private List<TestFile> classifyEmail(File dir, String actualClass) {
        List<TestFile> results = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    double logOdd = 0.0;
                    List<String> words = spamDetector.parseFile(file);

                    for (String word : words) {
                        if (spamDetector.getProbability().containsKey(word)) {
                            double spamGivenWord = spamDetector.getProbability().get(word) + 1e-9;
                            logOdd += Math.log(1.0 - spamGivenWord) - Math.log(spamGivenWord);
                        }
                    }

                    double pSpamGivenFile = 1.0 / (1.0 + Math.exp(logOdd));
                    results.add(new TestFile(file.getName(), pSpamGivenFile, actualClass));
                }
            }
        }
        return results;
    }
}
