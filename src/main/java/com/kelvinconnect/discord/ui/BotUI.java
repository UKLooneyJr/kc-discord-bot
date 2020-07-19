package com.kelvinconnect.discord.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BotUI {
    private final JDialog dialog;

    public BotUI() {
        dialog = new JDialog();
        dialog.setTitle("KC Bot");
        dialog.setSize(150, 75);
        dialog.setLayout(null);
        dialog.setResizable(false);

        JButton btnQuit = new JButton("Quit");
        btnQuit.setBounds(35, 10, 80, 25);
        btnQuit.addActionListener(e -> System.exit(0));
        dialog.add(btnQuit);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void show() {
        dialog.setVisible(true);
    }
}
