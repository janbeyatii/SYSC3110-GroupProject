package src;
/*
import GUI.ScrabbleController;
import org.json.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BoardConfigParser {
    public static List<PremiumSquare> loadFromJSON(String filePath) {
        List<PremiumSquare> premiumSquares = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject boardConfig = new JSONObject(content);
            JSONArray squaresArray = boardConfig.getJSONArray("premiumSquares");

            for (int i = 0; i < squaresArray.length(); i++) {
                JSONObject square = squaresArray.getJSONObject(i);
                int row = square.getInt("row");
                int col = square.getInt("col");
                String type = square.getString("type");

                premiumSquares.add(new PremiumSquare(row, col, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return premiumSquares;
    }
}
*/