package com.kelvinconnect.discord.command;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.fx.FxQuote;
import yahoofinance.quotes.fx.FxSymbols;
import yahoofinance.quotes.stock.StockQuote;

public class StockCommand implements BotCommand {
    private static final Logger logger = LogManager.getLogger(StockCommand.class);
    private final HashMap<Calendar, BigDecimal> startDatePrices = new HashMap<>();

    @Override
    public String[] getAliases() {
        return new String[] {"!stock"};
    }

    @Override
    public String getDescription() {
        return "Display the current NYSE:MSI stock price";
    }

    @Override
    public String getUsage() {
        return "!stock";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        try {
            EmbedBuilder embed = new EmbedBuilder();
            Stock stock = YahooFinance.get("MSI");
            setTitleWithQuote(embed, stock);
            setDescription(embed, stock);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        } catch (IOException e) {
            logger.error("Error getting stock.", e);
            event.getChannel().sendMessage("Error getting stock.").queue();
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
            if (change.signum() == -1) {
                sb.append("📉");
                embed.setColor(Color.RED);
            } else {
                sb.append("📈");
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

        ZonedDateTime summerStartDate = currentDate.withDayOfYear(isLeapYear ? 92 : 91);
        ZonedDateTime winterStartDate = currentDate.withDayOfYear(isLeapYear ? 275 : 274);
        boolean isSummer =
                (currentDate.isAfter(summerStartDate) && currentDate.isBefore(winterStartDate));

        ZonedDateTime firstDayOfYear = currentDate.withDayOfYear(1);
        if (!isSummer
                && (currentDate.isBefore(summerStartDate)
                        && currentDate.isAfter(firstDayOfYear))) {
            winterStartDate = winterStartDate.minusYears(1);
        }

        Calendar startDate = GregorianCalendar.from(isSummer ? summerStartDate : winterStartDate);

        BigDecimal currentPrice = stock.getQuote().getPrice();
        BigDecimal startPrice;

        if (startDatePrices.get(startDate) != null) {
            startPrice = startDatePrices.get(startDate);
        } else {
            Calendar endDate = (Calendar) startDate.clone();
            endDate.add(Calendar.DAY_OF_YEAR, 1);

            List<HistoricalQuote> stockHistory =
                    stock.getHistory(startDate, endDate, Interval.DAILY);

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

        return (0.85 * startPrice.min(currentPrice).doubleValue());
    }
}
