package com.kelvinconnect.discord.command.stando.filter;

import java.util.Arrays;
import java.util.Random;
import net.dv8tion.jda.api.entities.Message;

public class SlurFilter implements StandoFilter {
    private final Random random = new Random();
    private final int hicOccurrence;
    private final int shOccurrence;

    public SlurFilter() {
        hicOccurrence = 5;
        shOccurrence = 2;
    }

    public SlurFilter(int hicOccurrence, int shOccurrence) {
        this.hicOccurrence = hicOccurrence;
        this.shOccurrence = shOccurrence;
    }

    private static int getBeerCount(String message) {
        final String[] beerEmojis = {
            "🍺",
            "🍻",
            "🍷",
            "🍸",
            "🍹",
            "🍾",
            "🍶"
        };
        final String[] words = message.split("\\s");
        int beers = 0;
        for (String word : words) {
            if (Arrays.asList(beerEmojis).contains(word)) {
                beers++;
            }
        }
        return beers;
    }

    @Override
    public String filter(String input) {
        return input;
    }

    @Override
    public String filterWithMessage(String input, Message message) {
        if (getBeerCount(message.getContentRaw()) > 6) {
            input = slur(input);
        }
        return input;
    }

    private String slur(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case ' ':
                    sb.append(random.nextInt(hicOccurrence) == 0 ? " ...hic! " : " ");
                    break;
                case 's':
                    sb.append(random.nextInt(shOccurrence) == 0 ? "sh" : "s");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }
}
