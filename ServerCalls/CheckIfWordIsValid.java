package ServerCalls;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

public class CheckIfWordIsValid {

    // Method to get word definition using Unirest
    public static String getWordDefinition(String word) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                    .asJson();
            if (response.getStatus() == 200) {  // Check if the request was successful
                return response.getBody().toString();  // Return the JSON response as a string
            } else {
                System.out.println("Word not found or API error: " + response.getStatus());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to validate if the word exists (check if it has a valid definition)
    public static boolean isValidWord(String word) {
        String jsonResponse = getWordDefinition(word);
        return jsonResponse != null && !jsonResponse.contains("No Definitions Found");
    }

    // Method to extract the first definition from the JSON response
    public static String extractDefinition(String jsonResponse) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);

        if (jsonArray.size() > 0) {
            JsonObject firstEntry = jsonArray.get(0).getAsJsonObject();
            JsonArray meanings = firstEntry.getAsJsonArray("meanings");
            if (meanings.size() > 0) {
                JsonObject firstMeaning = meanings.get(0).getAsJsonObject();
                JsonArray definitions = firstMeaning.getAsJsonArray("definitions");
                if (definitions.size() > 0) {
                    return definitions.get(0).getAsJsonObject().get("definition").getAsString();  // Return the first definition
                }
            }
        }
        return "No definition available.";
    }
}
