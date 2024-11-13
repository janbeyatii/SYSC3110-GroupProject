package tests;

import org.junit.Before;
import org.junit.Test;
import src.WordValidity;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class WordValidityTests {

    private static final String TEST_WORD_FILE = "tests/test_wordlist.txt";

    @Before
    public void setUp() throws Exception {
        // Create a sample word list file for testing
        Files.write(Paths.get(TEST_WORD_FILE), "hello\nworld\ntest\nscrabble\n".getBytes());
        WordValidity.loadWordsFromFile(TEST_WORD_FILE);
    }

    @Test
    public void testWordListLoadedSuccessfully() throws NoSuchFieldException {
        // Test if the word list loads without errors
        assertNotNull("Word list should be loaded.", WordValidity.class.getDeclaredField("wordList"));
    }

    @Test
    public void testValidWord() {
        // Test words that should be valid
        assertTrue("The word 'hello' should be valid.", WordValidity.isWordValid("hello"));
        assertTrue("The word 'scrabble' should be valid.", WordValidity.isWordValid("scrabble"));
    }

    @Test
    public void testInvalidWord() {
        // Test words that should not be valid
        assertFalse("The word 'invalidword' should not be valid.", WordValidity.isWordValid("invalidword"));
        assertFalse("The word 'java' should not be valid.", WordValidity.isWordValid("java"));
    }

}
