package me.lolkas.snake;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Window extends JPanel implements Runnable{
    public ArrayList<SnakePart> parts = new ArrayList<>();
    public final Font font = new Font("Verdana", Font.PLAIN, 40);
    public final FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
    public int direction = 0;
    public boolean alive = true;
    public boolean addingPart = false;
    public static JPanel INSTANCE;
    public boolean stop = false;

    public int aX;
    public int aY;
    public boolean isShown = false;
    public JPanel apple = new JPanel();

    public boolean menu = true;
    public JPanel menuPanel = new JPanel();
    public JButton quit = new JButton();
    public JButton reset = new JButton();

    public JLabel score = new JLabel();
    public AudioInputStream inputStream;
    public Clip clip;

    public Window() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        super();
        init();
    }

    private void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.addKeyListener(new SKeyListener());
        this.setFocusable(true);
        this.setPreferredSize(new Dimension(480, 480));
        this.setLayout(null);
        this.add(score);
        score.setFont(font);
        score.setText("0");
        score.setOpaque(false);
        score.setBounds(480 / 2 - (int)font.getStringBounds(score.getText(), frc).getWidth() / 2, 0, 480 / 2 + (int)font.getStringBounds(score.getText(), frc).getWidth() / 2, 100);
        addPart(160, 160, new Color(0, 125, 0));
        addPart(140, 160, Color.GREEN);
        addPart(120, 160, Color.GREEN);
        menuPanel.setBackground(new Color(0,0,0, 18));
        menuPanel.setBounds(0, 0, 480, 480);
        quit.setText("Quit");
        reset.setText("Restart");
        reset.setBounds(480 / 2 - 100 / 2, 150, 100, 20);
        reset.addActionListener(e -> {
            for(SnakePart part : parts){
                INSTANCE.remove(part);
            }
            parts.clear();
            INSTANCE.remove(apple);
            menu = false;
            addPart(160, 160, new Color(0, 125, 0));
            addPart(140, 160, Color.GREEN);
            addPart(120, 160, Color.GREEN);
            direction = 0;
            alive = true;
        });
        quit.setBounds(480 / 2 - 100 / 2, 200, 100, 20);
        quit.addActionListener(e -> {
            System.exit(0);
        });
        menuPanel.add(reset);
        menuPanel.add(quit);
        INSTANCE = this;
        inputStream = AudioSystem.getAudioInputStream(new File("pop.wav").getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(inputStream);
        new Thread(this).start();
    }

    public void addPart(int x, int y, Color color){
        SnakePart part = new SnakePart(x, y, color);
        parts.add(part);
        this.add(part);
    }

    public void update(){
        int xDelta = 0;
        int yDelta = 0;
        ArrayList<SnakePart> tempPart = new ArrayList<>();

        for(SnakePart part : parts){
            tempPart.add(new SnakePart(part.x, part.y, part.color));
        }

        switch (direction) {
            case 0 -> xDelta = 20;
            case 1 -> yDelta = 20;
            case 2 -> xDelta = -20;
            case 3 -> yDelta = -20;
        }

        if(!addingPart){
            for(int i = 0; i < parts.size(); i++){
                if(i == 0)parts.get(i).update(xDelta, yDelta);
                else parts.get(i).set(tempPart.get(i - 1).x, tempPart.get(i - 1).y);
            }
        }else {
            SnakePart part = new SnakePart(parts.get(0).x, parts.get(0).y, Color.green);
            ArrayList<SnakePart> newParts = new ArrayList<>();
            parts.get(0).update(xDelta, yDelta);
            newParts.add(parts.get(0));
            newParts.add(part);
            for(SnakePart part1 : parts){
                if(part1 == parts.get(0)) continue;
                newParts.add(part1);
            }
            parts = newParts;
            addingPart = false;
            score.setText(parts.size() - 3 + "");
            score.setBounds(480 / 2 - (int)font.getStringBounds(score.getText(), frc).getWidth() / 2, 0, 480 / 2 + (int)font.getStringBounds(score.getText(), frc).getWidth() / 2, 100);
            this.add(part);
        }
    }

    public void collision(){
        if(parts.get(0).x > 480 || parts.get(0).x < 0 || parts.get(0).y > 480 || parts.get(0).y < 0) alive = false;
        for(SnakePart part : parts){
            if(part == parts.get(0)) continue;
            if (part.x == parts.get(0).x && part.y == parts.get(0).y) {
                alive = false;
                break;
            }
        }
    }

    public void apple(){
        boolean overlapping = false;
        if(!isShown){
            do{
                aX = (new Random().nextInt(23) + 1) * 20;
                aY = (new Random().nextInt(23) + 1) * 20;
                for(SnakePart part : parts){
                    if(part.x == aX && part.y == aY) {
                        overlapping = true;
                        break;
                    }
                }
            }while (overlapping);
            apple.setBackground(Color.RED);
            apple.setBounds(aX, aY, 20, 20);
            isShown = true;
        }

        if(parts.get(0).x == aX && parts.get(0).y == aY){
            isShown = false;
            addingPart = true;
            clip.close();
            clip.stop();
            try{
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("pop.wav").getAbsoluteFile());
                clip.open(inputStream);
            }catch (Exception e){
                e.printStackTrace();
            }
            clip.setMicrosecondPosition(0);
            clip.start();
        }
    }

    @Override
    public void run(){
        menuPanel.setVisible(false);
        this.add(menuPanel);
        while(true){
            this.add(apple);
            while (alive){
                stop = false;
                try{
                    Thread.sleep(200);
                }catch (Exception e){
                    e.printStackTrace();
                }
                update();
                collision();
                apple();
            }
            menu = true;
            menuPanel.setVisible(true);
            while(menu){
                try{
                    Thread.sleep(1);
                }catch (Exception ignored){}
            }
            menuPanel.setVisible(false);
        }
    }

    private class SKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if(stop) return;
            int key = e.getKeyCode();

            if(key == KeyEvent.VK_RIGHT && direction != 2){
                direction = 0;
            }
            if(key == KeyEvent.VK_DOWN && direction != 3){
                direction = 1;
            }
            if(key == KeyEvent.VK_LEFT && direction != 0){
                direction = 2;
            }
            if(key == KeyEvent.VK_UP && direction != 1){
                direction = 3;
            }
            stop = true;
        }
    }
}
