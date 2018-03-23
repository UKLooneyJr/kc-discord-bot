package com.kelvinconnect.discord.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BotFrame extends JFrame {

    private JButton btnQuit;

    public BotFrame() {
        setTitle("KC Bot");
        setSize(180, 75);
        setLayout(null);
        setResizable(false);

        btnQuit  = new JButton("Quit");
        btnQuit.setBounds(50, 10, 80, 25);
        btnQuit.addActionListener(e -> System.exit(0));
        add(btnQuit);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
