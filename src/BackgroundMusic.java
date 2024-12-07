package src;

import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class BackgroundMusic {
    private Player player;
    private Thread musicThread;
    private boolean isPlaying;

    /**
     * Play MP3 music file in a loop.
     *
     * @param filePath The path to the MP3 file.
     */
    public void playMusic(String filePath) {
        stopMusic(); // Stop any currently playing music

        isPlaying = true; // Set the flag to indicate music is playing
        musicThread = new Thread(() -> {
            while (isPlaying) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
                    player = new Player(bis);
                    player.play();
                } catch (Exception e) {
                    System.err.println("Error playing music: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        });

        musicThread.start(); // Start the music thread
    }

    /**
     * Stop the currently playing music.
     */
    public void stopMusic() {
        isPlaying = false;
        if (player != null) {
            player.close();
        }
        if (musicThread != null && musicThread.isAlive()) {
            try {
                musicThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
