package com.kelvinconnect.discord.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BotUI extends JDialog {

    public BotUI() {
        setTitle("KC Bot");
        setSize(150, 75);
        setLayout(null);
        setResizable(false);

        JButton btnQuit = new JButton("Quit");
        btnQuit.setBounds(35, 10, 80, 25);
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
