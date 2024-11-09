package src;


import java.util.List;
import java.util.ArrayList;
import javax.swing.*;


public class ScrabbleController {
    static int playercount = Integer.parseInt(JOptionPane.showInputDialog("Number of players (2-4): "));
    static ArrayList<String> playerNames = new ArrayList<>();

    public static int getPlayercount() {
        if (playercount > 4 || playercount < 2) {
                while (playercount > 4 || playercount < 2 ){
                    playercount = Integer.parseInt(JOptionPane.showInputDialog("Number of players (2-4): "));
                }
            }
        return playercount;
    }
    public static ArrayList<String> getPlayerNames() {
        String player = JOptionPane.showInputDialog("Player Name: ");
        playerNames.add(player);
        return playerNames;
    }

    public static void main(String[] args) {
         List<List<Character>> playerTiles = new ArrayList<>();
         int currentPlayer = 0;
         List<Character> currentPlayerTiles = playerTiles.get(currentPlayer);
    }
    }


