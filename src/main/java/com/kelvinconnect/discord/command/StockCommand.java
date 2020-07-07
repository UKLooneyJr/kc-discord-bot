package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;
import yahoofinance.quotes.fx.FxSymbols;
import yahoofinance.quotes.stock.StockQuote;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class StockCommand implements CommandExecutor {
    private static final Logger logger = LogManager.getLogger(StockCommand.class);

    @Command(aliases = "!stock", description = "Display the current NYSE:MSI stock price", usage = "!stock")
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
        sb.append("***");
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
        // MSI as of <current date>
        sb.append("**");
        sb.append(stock.getSymbol());
        sb.append("** as of ");
        sb.append(ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME));
        sb.append("\n\n");

        sb.append("Year High: ");
        sb.append(quote.getYearHigh());
        sb.append("\n");

        sb.append("Year Low: ");
        sb.append(quote.getYearLow());
        sb.append("\n\n");

        FxQuote usdgbp = YahooFinance.getFx(FxSymbols.USDGBP);
        sb.append("Current USD/GPB rate: $1=£");
        sb.append(usdgbp.getPrice());
        embed.setDescription(sb.toString());
    }
}
