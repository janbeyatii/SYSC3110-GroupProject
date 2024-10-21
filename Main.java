import src.WordValidity;

public class Main {
    public static void main(String[] args) {
        WordValidity.loadWordsFromFile("wordLists/wordlist.txt");


        if (WordValidity.isWordValid("word")) {
            System.out.println("word" + " is a valid word.");
        } else {
            System.out.println("word" + " is not a valid word.");
        }
    }
}
