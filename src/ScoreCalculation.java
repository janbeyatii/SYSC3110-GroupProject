package src;
import java.util.*;

public class ScoreCalculation {
    int totalscore;
    public  ScoreCalculation (String word) {
        ArrayList<Character> score1 = new ArrayList<Character>();
        score1.add('A');
        score1.add('E');
        score1.add('I');
        score1.add('O');
        score1.add('U');
        score1.add('L');
        score1.add('N');
        score1.add('S');
        score1.add('T');
        score1.add('R');
        ArrayList<Character> score2 = new ArrayList<Character>();
        score2.add('G');
        score2.add('D');
        ArrayList<Character> score3 = new ArrayList<Character>();
        score3.add('B');
        score3.add('C');
        score3.add('M');
        score3.add('P');
        ArrayList<Character> score4 = new ArrayList<Character>();
        score4.add('F');
        score4.add('H');
        score4.add('V');
        score4.add('W');
        score4.add('Y');
        ArrayList<Character> score5 = new ArrayList<Character>();
        score5.add('K');
        ArrayList<Character> score8 = new ArrayList<Character>();
        score8.add('J');
        score8.add('X');
        ArrayList<Character> score10 = new ArrayList<Character>();
        score10.add('Q');
        score10.add('Z');

        for (int k = 0; k < word.length(); k++) {
            for (int i = 0; i < score1.size(); i++) {
                if (word.charAt(k) == score1.get(i)) {
                    totalscore += 1;
                }
            }
            for (int i = 0; i < score2.size(); i++) {
                if (word.charAt(k) == score2.get(i)) {
                    totalscore += 2;
                }
            }
            for (int i = 0; i < score3.size(); i++) {
                if (word.charAt(k) == score3.get(i)) {
                    totalscore += 3;
                }
            }
            for (int i = 0; i < score4.size(); i++) {
                if (word.charAt(k) == score4.get(i)) {
                    totalscore += 4;
                }
            }
            for (int i = 0; i < score5.size(); i++) {
                if (word.charAt(k) == score5.get(i)) {
                    totalscore += 5;
                }
            }
            for (int i = 0; i < score8.size(); i++) {
                if (word.charAt(k) == score8.get(i)) {
                    totalscore += 8;
                }
            }
            for (int i = 0; i < score10.size(); i++) {
                if (word.charAt(k) == score10.get(i)) {
                    totalscore += 10;
                }
            }
        }
        System.out.println(totalscore);
    }
    public static void main(String[] args) {
        ScoreCalculation sc = new ScoreCalculation("HELLO");
    }
}

