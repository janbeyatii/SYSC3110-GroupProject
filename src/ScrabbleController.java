package src;

import javax.swing.*;
import java.util.ArrayList;

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
}

