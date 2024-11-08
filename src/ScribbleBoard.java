import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ScribbleBoard extends JFrame {

    private static final int BOARD_WIDTH = 800;
    private static final int BOARD_HEIGHT = 600;

    // Constructor for the ScribbleBoard JFrame
    public ScribbleBoard() {
        setTitle("Scribble Board");
        setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create a drawing canvas
        DrawingCanvas canvas = new DrawingCanvas();
        add(canvas, BorderLayout.CENTER);

        // Panel for letter buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Create letter buttons
        String[] letters = {"A", "B", "C", "D", "E"};
        for (String letter : letters) {
            JButton button = new JButton(letter);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(e -> canvas.drawLetter(letter));
            buttonPanel.add(button);
        }

        // Add the button panel to the bottom of the frame
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Inner class for the drawing canvas
    private static class DrawingCanvas extends JPanel {
        private Image image;
        private Graphics2D g2d;
        private int prevX, prevY;

        public DrawingCanvas() {
            setDoubleBuffered(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    prevX = e.getX();
                    prevY = e.getY();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();

                    if (g2d != null) {
                        g2d.drawLine(prevX, prevY, x, y);
                        repaint();
                        prevX = x;
                        prevY = y;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image == null) {
                image = createImage(getSize().width, getSize().height);
                g2d = (Graphics2D) image.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                clear();
            }
            g.drawImage(image, 0, 0, null);
        }

        // Method to clear the canvas
        public void clear() {
            g2d.setPaint(Color.white);
            g2d.fillRect(0, 0, getSize().width, getSize().height);
            g2d.setPaint(Color.black);
            repaint();
        }

        // Method to draw a letter on the canvas at a random location
        public void drawLetter(String letter) {
            if (g2d != null) {
                // Generate random coordinates within the canvas
                int x = (int) (Math.random() * (getSize().width - 50));
                int y = (int) (Math.random() * (getSize().height - 50));

                // Set font and color for the letter
                g2d.setFont(new Font("Arial", Font.BOLD, 36));
                g2d.setColor(Color.BLUE);
                g2d.drawString(letter, x, y);
                repaint();
            }
        }
    }

    // Main method to launch the ScribbleBoard
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScribbleBoard board = new ScribbleBoard();
            board.setVisible(true);
        });
    }
}
