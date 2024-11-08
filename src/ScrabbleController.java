package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ScrabbleController {
    static int playercount = Integer.parseInt(JOptionPane.showInputDialog("Number of players (2-4): "));
    public static int getPlayercount() {
        if (playercount > 4 || playercount < 2) {
                while (playercount > 4 || playercount < 2 ){
                    playercount = Integer.parseInt(JOptionPane.showInputDialog("Number of players (2-4): "));
                }
            }
        return playercount;
    }
}

