package src;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WordValidity {
    private static List<String> wordList;

    // load words from the text file
    public static void loadWordsFromFile(String filePath) {
        try {
            wordList = Files.readAllLines(Paths.get(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // check if the word is valid
    public static boolean isWordValid(String word) {
        if (wordList == null) {
            throw new IllegalStateException("Word list is not loaded. Please load the word list first.");
        }
        return wordList.contains(word.toLowerCase());
    }
}
