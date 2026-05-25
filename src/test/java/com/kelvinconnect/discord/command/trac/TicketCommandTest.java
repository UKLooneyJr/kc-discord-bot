package com.kelvinconnect.discord.command.trac;

import static org.junit.Assert.*;

import org.junit.Test;

/** Created by Adam on 15/03/2017. */
public class TicketCommandTest {

    @Test
    public void tooManyArguments() {
        TicketCommand command = new TicketCommand();
        String output = command.validateArgs(new String[] {"1234", "5678"});
        assertEquals("Incorrect number of arguments!", output);
    }

    @Test
    public void noArguments() {
        TicketCommand command = new TicketCommand();
        String output = command.validateArgs(new String[] {});
        assertEquals("Incorrect number of arguments!", output);
    }

    @Test
    public void invalidArgument() {
        TicketCommand command = new TicketCommand();
        String input = "one,two,three,four";
        String output = command.validateArgs(new String[] {input});
        assertEquals(input + " is not a valid ticket number.", output);
    }
}
