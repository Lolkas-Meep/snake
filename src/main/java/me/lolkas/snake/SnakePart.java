package me.lolkas.snake;

import javax.swing.*;
import java.awt.*;

public class SnakePart extends JPanel {
    public int x;
    public int y;
    public Color color;

    public SnakePart(int x, int y, Color color){
        super();
        this.x = x;
        this.y = y;
        this.color = color;
        this.setBackground(color);
        this.setBounds(x ,y ,20, 20);
    }

    public void update(int x, int y){
        this.x = this.x + x;
        this.y = this.y + y;
        this.setBounds(this.x ,this.y ,20, 20);
    }

    public void set(int x, int y){
        this.x = x;
        this.y = y;
        this.setBounds(this.x ,this.y ,20, 20);
    }
}
