package src;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * The WordValidity class provides methods to validate words in a Scrabble game.
 * It checks if a word is valid by comparing it to a loaded word list from a file.
 */
public class WordValidity {
    private static List<String> wordList;

    /**
     * Default constructor for the WordValidity class.
     * Initializes the WordValidity object without any specific setup.
     */
    public WordValidity() {
        // Default constructor
    }

    /**
     * Loads a list of valid words from a specified text file.
     * The words are stored in the wordList, which is used for validation.
     *
     * @param filePath the path to the text file containing valid Scrabble words.
     */
    public static void loadWordsFromFile(String filePath) {
        try {
            wordList = Files.readAllLines(Paths.get(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the given word is valid by checking if it exists in the word list.
     *
     * @param word the word to be checked for validity.
     * @return true if the word is valid, false otherwise.
     * @throws IllegalStateException if the word list has not been loaded yet.
     */
    public static boolean isWordValid(String word) {
        if (wordList == null) {
            throw new IllegalStateException("Word list is not loaded. Please load the word list first.");
        }
        return wordList.contains(word.toLowerCase());
    }
}
