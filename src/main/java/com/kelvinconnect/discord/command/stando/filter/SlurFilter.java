package com.kelvinconnect.discord.command.stando.filter;

import de.btobastian.javacord.entities.message.Message;

import java.util.Arrays;
import java.util.Random;

public class SlurFilter implements StandoFilter {
    @Override
    public String filter(String input) {
        // We only want to slur if the message contains beer emoji
        return input;
    }

    @Override
    public String filterWithMessage(String input, Message message) {
        if (getBeerCount(message.getContent()) > 6) {
            input = slur(input);
        }
        return input;
    }

    private static int getBeerCount(String message) {
        final String[] beerEmojis = {"\uD83C\uDF7A", "\uD83C\uDF7B", "\uD83C\uDF77",
                "\uD83C\uDF78", "\uD83C\uDF79", "\uD83C\uDF7E", "\uD83C\uDF76"};
        final String[] words = message.split("\\s");
        int beers = 0;
        for (String word : words) {
            if (Arrays.stream(beerEmojis).anyMatch(x -> x.equals(word))) {
                beers++;
            }
        }
        return beers;
    }

    private String slur(String input) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case ' ':
                    sb.append(r.nextInt(5) == 0 ? " ...hic! " : " ");
                    break;
                case 's':
                    sb.append(r.nextBoolean() ? "sh" : "s");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}
