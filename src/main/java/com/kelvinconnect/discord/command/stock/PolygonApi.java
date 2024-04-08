package com.kelvinconnect.discord.command.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelvinconnect.discord.command.stock.dto.Historical;
import com.kelvinconnect.discord.command.stock.dto.Stock;
import com.kelvinconnect.discord.command.stock.dto.TickerInfo;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PolygonApi {

    public PolygonApi(String apiKey) {
        this.API_KEY = apiKey;
    }

    private final String API_KEY;
    DateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Stock getStock(String ticker) throws IOException {
        return this.getStockWithInterval(ticker, "day");
    }

    public Stock getStockForYear(String ticker) throws IOException {
        return this.getStockWithInterval(ticker, "year");
    }

    public Stock getStockWithInterval(String ticker, String interval) throws IOException {
        Calendar todayDate = new GregorianCalendar();
        Calendar coverTheWeekend = new GregorianCalendar();
        coverTheWeekend.add(Calendar.DATE, -3);
        String today = apiDateFormat.format(todayDate.getTime());
        String startDate = apiDateFormat.format(coverTheWeekend.getTime());

        URL apiUrl = new URL("https://api.polygon.io/v2/aggs/ticker/" + ticker +
                "/range/1/" + interval + "/" + startDate + "/" + today + "?adjusted=true&sort=desc&limit=120&apiKey=" + API_KEY);
        return new ObjectMapper().readValue(apiUrl, Stock.class);
    }

    public Historical getHistoricalQuote(String ticker, Date date) throws IOException {
        String apiDate = apiDateFormat.format(date);
        URL apiUrl = new URL("https://api.polygon.io/v1/open-close/" + ticker + "/" + apiDate + "?adjusted=true&apiKey=" + API_KEY);
        return new ObjectMapper().readValue(apiUrl, Historical.class);
    }

    public URL getImageUrl(String ticker) throws IOException {
        URL apiUrl = new URL("https://api.polygon.io/v3/reference/tickers/" + ticker + "?apiKey=" + API_KEY);
        TickerInfo info = new ObjectMapper().readValue(apiUrl, TickerInfo.class);
        if(info.getResults().getBranding().getIconUrl() != null){
            return new URL(info.getResults().getBranding().getIconUrl() + "?apiKey=" + API_KEY);
        }
        //If we don't have an image, just use something generic
        return new URL("https://upload.wikimedia.org/wikipedia/commons/d/dc/Dollar-teken.png");
    }

}
