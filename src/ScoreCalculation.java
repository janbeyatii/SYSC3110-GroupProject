package src;
import java.util.*;

public class ScoreCalculation {
    private int totalscore;

    // Constructor that calculates the total score of the word
    public ScoreCalculation(String word) {
        ArrayList<Character> score1 = new ArrayList<>(Arrays.asList('A', 'E', 'I', 'O', 'U', 'L', 'N', 'S', 'T', 'R'));
        ArrayList<Character> score2 = new ArrayList<>(Arrays.asList('G', 'D'));
        ArrayList<Character> score3 = new ArrayList<>(Arrays.asList('B', 'C', 'M', 'P'));
        ArrayList<Character> score4 = new ArrayList<>(Arrays.asList('F', 'H', 'V', 'W', 'Y'));
        ArrayList<Character> score5 = new ArrayList<>(Arrays.asList('K'));
        ArrayList<Character> score8 = new ArrayList<>(Arrays.asList('J', 'X'));
        ArrayList<Character> score10 = new ArrayList<>(Arrays.asList('Q', 'Z'));

        for (int k = 0; k < word.length(); k++) {
            char letter = Character.toUpperCase(word.charAt(k));  // Convert to uppercase
            if (score1.contains(letter)) {
                totalscore += 1;
            } else if (score2.contains(letter)) {
                totalscore += 2;
            } else if (score3.contains(letter)) {
                totalscore += 3;
            } else if (score4.contains(letter)) {
                totalscore += 4;
            } else if (score5.contains(letter)) {
                totalscore += 5;
            } else if (score8.contains(letter)) {
                totalscore += 8;
            } else if (score10.contains(letter)) {
                totalscore += 10;
            }
        }
    }

    // return the total score of a word
    public int getTotalScore() {
        return totalscore;
    }
}
