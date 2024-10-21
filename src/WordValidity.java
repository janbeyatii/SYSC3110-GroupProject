package src;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WordValidity {
    private static List<String> wordList;

    // Function to load words from a text file
    public static void loadWordsFromFile(String filePath) {
        try {
            wordList = Files.readAllLines(Paths.get(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to check if a word is valid
    public static boolean isWordValid(String word) {
        // Check if wordList is null and handle it accordingly
        if (wordList == null) {
            throw new IllegalStateException("Word list is not loaded. Please load the word list first.");
        }
        return wordList.contains(word);
    }
}
