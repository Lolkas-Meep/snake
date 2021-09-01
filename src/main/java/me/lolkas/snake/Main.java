package me.lolkas.snake;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main extends JFrame {
    public Main() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        super();
        add(new Window());

        setResizable(false);
        pack();
        setTitle("snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame snake = null;
            try {
                snake = new Main();
            } catch (Exception e) {
                e.printStackTrace();
            }
            snake.setVisible(true);
        });
    }
}
