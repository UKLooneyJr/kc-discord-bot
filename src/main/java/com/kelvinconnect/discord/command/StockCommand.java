package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.fx.FxQuote;
import yahoofinance.quotes.fx.FxSymbols;
import yahoofinance.quotes.stock.StockQuote;

public class StockCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(StockCommand.class);
    private final HashMap<Calendar, BigDecimal> startDatePrices = new HashMap<>();

    @Command(
            aliases = "!stock",
            description = "Display the current NYSE:MSI stock price",
            usage = "!stock")
    public String onStockCommand(Message message) {
        try {
            EmbedBuilder embed = new EmbedBuilder();

            Stock stock = YahooFinance.get("MSI");

            setTitleWithQuote(embed, stock);
            setDescription(embed, stock);
            message.getChannel().sendMessage(embed);

            return null;
        } catch (IOException e) {
            logger.error("Error getting stock.", e);
            return "Error getting stock.";
        }
    }

    private void setTitleWithQuote(EmbedBuilder embed, Stock stock) {
        StockQuote quote = stock.getQuote();
        StringBuilder sb = new StringBuilder();
        sb.append("***$");
        sb.append(quote.getPrice());
        sb.append("*** ");
        BigDecimal change = quote.getChange();
        if (null != change) {
            // test if change is negative
            if (change.signum() == -1) {
                // chart decreasing
                sb.append("\uD83D\uDCC9");
                embed.setColor(Color.RED);
            } else {
                sb.append("\uD83D\uDCC8");
                embed.setColor(Color.GREEN);
            }
            sb.append("  ");
            sb.append(change);
            sb.append(" (");
            sb.append(quote.getChangeInPercent());
            sb.append("%)");
        }
        embed.setTitle(sb.toString());
    }

    private void setDescription(EmbedBuilder embed, Stock stock) throws IOException {
        StockQuote quote = stock.getQuote();
        StringBuilder sb = new StringBuilder();

        ZonedDateTime currentDate = ZonedDateTime.now();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);

        // MSI as of <current date>
        sb.append("**[");
        sb.append(stock.getSymbol());
        sb.append("](https://www.google.com/finance/quote/");
        sb.append(stock.getSymbol()).append(":");
        sb.append(stock.getStockExchange());
        sb.append(")** as of ");
        sb.append(currentDate.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        sb.append("\n\n");

        sb.append("Year High: ");
        sb.append(formatter.format(quote.getYearHigh()));
        sb.append("\n");

        sb.append("Year Low: ");
        sb.append(formatter.format(quote.getYearLow()));
        sb.append("\n\n");

        sb.append("ESPP Price: ");
        sb.append(formatter.format(getEsppPrice(stock)));
        sb.append("\n\n");

        FxQuote usdGbp = YahooFinance.getFx(FxSymbols.USDGBP);
        sb.append("Current USD/GBP rate: $1=£");
        sb.append(usdGbp.getPrice());

        embed.setDescription(sb.toString());
    }

    private double getEsppPrice(Stock stock) throws IOException {
        ZonedDateTime currentDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        boolean isLeapYear = currentDate.toLocalDate().isLeapYear();

        // 2021-04-01 = 91st day of year
        ZonedDateTime summerStartDate = currentDate.withDayOfYear(isLeapYear ? 92 : 91);
        // 2021-10-01 = 274th day of year
        ZonedDateTime winterStartDate = currentDate.withDayOfYear(isLeapYear ? 275 : 274);
        boolean isSummer =
                (currentDate.isAfter(summerStartDate) && currentDate.isBefore(winterStartDate));

        ZonedDateTime firstDayOfYear = currentDate.withDayOfYear(1);
        if (!isSummer
                && (currentDate.isBefore(summerStartDate) && currentDate.isAfter(firstDayOfYear))) {
            // Between January 1st and April 1st, we're interested in last year's October
            winterStartDate = winterStartDate.minusYears(1);
        }

        Calendar startDate = GregorianCalendar.from(isSummer ? summerStartDate : winterStartDate);

        BigDecimal currentPrice = stock.getQuote().getPrice();
        BigDecimal startPrice;

        if (startDatePrices.get(startDate) != null) {
            startPrice = startDatePrices.get(startDate);
        } else {
            Calendar endDate = (Calendar) startDate.clone();
            // we only really need one day's worth of history
            endDate.add(Calendar.DAY_OF_YEAR, 1);

            // fetch stock history only if we don't have this start date already
            List<HistoricalQuote> stockHistory =
                    stock.getHistory(startDate, endDate, Interval.DAILY);

            // Okay, so when we get this historical data, we actually don't necessarily know if the
            // date we selected
            // has any data, and if it doesn't, we can end up a weekend off.
            // So here, we prefer to select a quote from the exact date, and if that doesn't exist,
            // we just take
            // the first one in the collection
            Optional<HistoricalQuote> startDateQuote =
                    stockHistory.stream()
                            .filter(q -> q.getDate().toInstant().equals(startDate.toInstant()))
                            .findFirst();

            startPrice =
                    startDateQuote
                            .map(HistoricalQuote::getClose)
                            .orElseGet(() -> stockHistory.get(0).getClose());

            startDatePrices.put(startDate, startPrice);
        }

        // ESPP purchase price is 15% off either the first day's closing price, or the last day's,
        // whichever is lower.
        // We never know the last day's price until it's over, so we can guess with today's (or most
        // recent).
        return (0.85 * startPrice.min(currentPrice).doubleValue());
    }
}
