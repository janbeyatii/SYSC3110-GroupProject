import static ServerCalls.CheckIfWordIsValid.*;

public static void main(String[] args) {
    String word = "jacket";  // Example word
    if (isValidWord(word)) {
        String jsonResponse = getWordDefinition(word);
        String definition = extractDefinition(jsonResponse);
        System.out.println(word + " is a valid word.");
        System.out.println("Definition: " + definition);
    } else {
        System.out.println(word + " is not a valid word.");
    }
}
