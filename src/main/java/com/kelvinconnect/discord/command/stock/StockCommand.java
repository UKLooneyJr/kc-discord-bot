package com.kelvinconnect.discord.command.stock;

import com.kelvinconnect.discord.command.stock.dto.Historical;
import com.kelvinconnect.discord.command.stock.dto.Result;
import com.kelvinconnect.discord.command.stock.dto.Stock;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class StockCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(StockCommand.class);
    private final HashMap<Calendar, Double> startDatePrices = new HashMap<>();

    private Calendar lastLookup = new GregorianCalendar(); //Need to throttle for the API rate limit

    private final String API_KEY;

    public StockCommand(String apiKey) {
        this.API_KEY = apiKey;
    }

    @Command(
            aliases = "!stock",
            description = "Display the current stock price",
            usage = "!stock <ticker>")
    public String onStockCommand(Message message) {
        String ticker = message.getContent().replace("!stock", "").trim().toUpperCase();
        if (ticker.equals("")) {
            //No ticker supplied, default to MSI
            ticker = "MSI";
        }
        Calendar now = new GregorianCalendar();
        long timeDifference = now.getTimeInMillis() - lastLookup.getTimeInMillis();
        if (timeDifference > 30000) {
            try {
                lastLookup = now;
                PolygonApi api = new PolygonApi(API_KEY);
                EmbedBuilder embed = new EmbedBuilder();

                Stock stock = api.getStock(ticker);
                Stock historicStock = api.getStockForYear(ticker);
                setTitleWithQuote(embed, stock);
                setDescription(embed, stock, historicStock);
                embed.setThumbnail(api.getImageUrl(ticker).toString());
                message.getChannel().sendMessage(embed);

                return null;
            } catch (IOException e) {
                logger.error("Error getting stock.", e);
                return "Error getting stock.";
            }
        } else {
            return "Slow down";
        }
    }

    private void setTitleWithQuote(EmbedBuilder embed, Stock stock) {
        DecimalFormat df = new DecimalFormat("0.00");
        StringBuilder sb = new StringBuilder();
        sb.append("***$");
        Result result = stock.getResults().get(0);
        sb.append(result.getC().toString());
        sb.append("*** ");
        double diff = result.getC() - result.getO();
        if (diff > 0.0) {
            sb.append("\uD83D\uDCC8");
            embed.setColor(Color.GREEN);
        } else {
            sb.append("\uD83D\uDCC9");
            embed.setColor(Color.RED);
        }

        sb.append("  ");
        sb.append(df.format(diff));
        sb.append(" (");
        sb.append(df.format((diff / result.getC()) * 100));
        sb.append("%)");

        embed.setTitle(sb.toString());
    }

    private void setDescription(EmbedBuilder embed, Stock currentStock, Stock historicStock) throws IOException {
        StringBuilder sb = new StringBuilder();

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

        // MSI as of <current date>
        sb.append("**[");
        String ticker = currentStock.getTicker();
        sb.append(ticker);
        sb.append("](https://finance.yahoo.com/quote/");
        sb.append(ticker);
        Result result = currentStock.getResults().get(0);
        sb.append(")** as of ");
        sb.append(new Date(result.getT()));
        sb.append("\n\n");
        Result yearResult = historicStock.getResults().get(0);
        sb.append("Year-To-Date High: ");
        sb.append(formatter.format(yearResult.getH()));
        sb.append("\n");

        sb.append("Year-To-Date Low: ");
        sb.append(formatter.format(yearResult.getL()));
        sb.append("\n\n");

        if (ticker.equals("MSI")) {//Ignore ESPP if non-MSI
            sb.append("ESPP Price: ");
            sb.append(formatter.format(getEsppPrice(currentStock)));
            sb.append("\n\n");
        }
        embed.setDescription(sb.toString());
    }


    private double getEsppPrice(Stock stock) throws IOException {
        PolygonApi api = new PolygonApi(API_KEY);
        ZonedDateTime currentDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime summerStartDate = currentDate.withMonth(4).withDayOfMonth(1);
        ZonedDateTime winterStartDate = currentDate.withMonth(10).withDayOfMonth(1);
        boolean isSummer =
                (currentDate.isAfter(summerStartDate) && currentDate.isBefore(winterStartDate));

        if (!isSummer && (currentDate.isBefore(summerStartDate))) {
            // Between January 1st and April 1st, we're interested in last year's October
            winterStartDate = winterStartDate.minusYears(1);
        }

        Calendar startDate = GregorianCalendar.from(isSummer ? summerStartDate : winterStartDate);
        String ticker = stock.getTicker();
        double currentPrice = stock.getResults().get(0).getC();
        double startPrice;

        if (startDatePrices.get(startDate) != null) {
            startPrice = startDatePrices.get(startDate);
        } else {
            Calendar endDate = (Calendar) startDate.clone();
            // Three days of history to cover weekends
            endDate.add(Calendar.DAY_OF_YEAR, 3);

            Historical startQuote = api.getHistoricalQuote(ticker, startDate.getTime());
            Historical endQuote = api.getHistoricalQuote(ticker, endDate.getTime()); // one of these might fail?

            if (startQuote != null) {
                startPrice = startQuote.getClose();
            } else {
                startPrice = endQuote.getClose();// this might not be accurate
            }
            startDatePrices.put(startDate, startPrice);
        }

        // ESPP purchase price is 15% off either the first day's closing price, or the last day's,
        // whichever is lower.
        // We never know the last day's price until it's over, so we can guess with today's (or most
        // recent).
        return (0.85 * Math.min(startPrice, currentPrice));
    }
}
