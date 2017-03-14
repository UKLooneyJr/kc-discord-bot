package com.kelvinconnect.discord.command;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates a link to a ticket on Trac
 *
 * Created by Adam on 14/03/2017.
 */
public class TicketCommand implements CommandExecutor {

    private static final String TICKET_FORMAT = "^(\\d|#)\\d*$";

    @Command(aliases = "!ticket", description = "Creates a trac link to the mentioned ticket.", usage = "!ticket [ticket-number]")
    public String onTicketCommand(String[] args) {
        if (args.length != 1) { // more than 1 argument
            return "Incorrect number of arguments!";
        }
        String ticketNumber = args[0].trim();

        Pattern p = Pattern.compile(TICKET_FORMAT);
        Matcher m = p.matcher(ticketNumber);
        if (!m.find()) {
            return ticketNumber + " is not a valid ticket number.";
        }

        if (ticketNumber.startsWith("#")) {
            ticketNumber = ticketNumber.substring(1);
        }

        return "http://trac/KC/ticket/" + ticketNumber;

    }
}
